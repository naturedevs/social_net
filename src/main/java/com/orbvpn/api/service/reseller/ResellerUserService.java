package com.orbvpn.api.service.reseller;

import com.orbvpn.api.domain.dto.ResellerUserCreate;
import com.orbvpn.api.domain.dto.ResellerUserEdit;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.domain.enums.*;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.InsufficientFundsException;
import com.orbvpn.api.mapper.ResellerUserCreateMapper;
import com.orbvpn.api.mapper.UserProfileEditMapper;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.PaymentRepository;
import com.orbvpn.api.reposiitory.ResellerRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.service.*;
import com.orbvpn.api.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.orbvpn.api.config.AppConstants.DEFAULT_SORT;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResellerUserService {

    private final ResellerUserCreateMapper resellerUserCreateMapper;
    private final UserViewMapper userViewMapper;
    private final UserProfileEditMapper userProfileEditMapper;

    private final PasswordService passwordService;
    private final UserService userService;
    private final RoleService roleService;
    private final GroupService groupService;
    private final UserSubscriptionService userSubscriptionService;
    private final ResellerService resellerService;
    private final ResellerSaleService resellerSaleService;

    private final UserRepository userRepository;
    private final ResellerRepository resellerRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;


    public UserView createUser(ResellerUserCreate resellerUserCreate) {
        log.info("Creating user");
        User creator = userService.getUser();
        Reseller reseller = creator.getReseller();
        Group group = groupService.getById(resellerUserCreate.getGroupId());

        Optional<User> userEntityOptional = userRepository.findByEmail(resellerUserCreate.getEmail());
        if (userEntityOptional.isPresent()) {
            throw new BadRequestException("User with specified email exists");
        }

        User user = resellerUserCreateMapper.create(resellerUserCreate);
        user.setUsername(resellerUserCreate.getEmail());
        passwordService.setPassword(user, resellerUserCreate.getPassword());
        Role role = roleService.getByName(RoleName.USER);
        user.setRole(role);
        user.setReseller(reseller);

        userRepository.save(user);
        createResellerUserSubscription(user, group);

        UserView userView = userViewMapper.toView(user);
        log.info("Created user");

        return userView;
    }

    public void createResellerUserSubscription(User user, Group group) {
        Reseller reseller = user.getReseller();

        BigDecimal credit = reseller.getCredit();
        BigDecimal price = calculatePrice(reseller, group);
        if (credit.compareTo(price) < 0) {
            throw new InsufficientFundsException();
        }
        reseller.setCredit(credit.subtract(price));
        resellerRepository.save(reseller);

        String paymentId = UUID.randomUUID().toString();

        Payment payment = Payment.builder()
                .user(user)
                .status(PaymentStatus.PENDING)
                .gateway(GatewayName.RESELLER_CREDIT)
                .category(PaymentCategory.GROUP)
                .price(group.getPrice())
                .groupId(group.getId())
                .paymentId(paymentId)
                .build();

        resellerSaleService.createSale(reseller, user, group, price);

        paymentRepository.save(payment);
        paymentService.fullFillPayment(payment);
    }


    public BigDecimal calculatePrice(Reseller reseller, Group group) {
        ResellerLevel level = reseller.getLevel();
        if (level.getName() == ResellerLevelName.OWNER) {
            return BigDecimal.ZERO;
        }

        BigDecimal price = group.getPrice();
        BigDecimal discount = price.multiply(level.getDiscountPercent()).divide(new BigDecimal(100));

        return price.subtract(discount);
    }

    public UserView editUserByEmail(String email, ResellerUserEdit resellerUserEdit) {
        User user = userService.getUserByEmail(email);
        return editUser(user, resellerUserEdit);
    }


    public UserView editUserById(int id, ResellerUserEdit resellerUserEdit) {
        User user = userService.getUserById(id);
        return editUser(user, resellerUserEdit);
    }

    public UserView editUser(User user, ResellerUserEdit resellerUserEdit) {
        log.info("Editing user with id {}", user.getId());
        checkResellerUserAccess(user);

        String password = resellerUserEdit.getPassword();
        if (password != null) {
            passwordService.setPassword(user, password);
        }

        Integer resellerId = resellerUserEdit.getResellerId();
        if (resellerId != null && userService.isAdmin()) {
            Reseller reseller = resellerService.getResellerById(resellerId);
            user.setReseller(reseller);
        }

        Integer groupId = resellerUserEdit.getGroupId();
        if (groupId != null) {
            Group group = groupService.getById(groupId);
            createResellerUserSubscription(user, group);
        }

        Integer multiLoginCount = resellerUserEdit.getMultiLoginCount();
        if (multiLoginCount != null) {
            userSubscriptionService.updateSubscriptionMultiLoginCount(user, multiLoginCount);
        }


        if (user.getProfile() == null) {
            UserProfile profile = new UserProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }
        userProfileEditMapper.edit(user.getProfile(), resellerUserEdit.getUserProfileEdit());

        userRepository.save(user);

        UserView userView = userViewMapper.toView(user);
        log.info("Edited user with id {}", user.getId());
        return userView;

    }

    public UserView deleteUserByEmail(String email) {
        return deleteUser(userService.getUserByEmail(email));
    }

    public UserView deleteUserById(int id) {
        return deleteUser(userService.getUserById(id));
    }

    public UserView deleteUser(User user) {
        log.info("Deleting user with id {}", user.getId());
        checkResellerUserAccess(user);
        userService.deleteUser(user);
        UserView userView = userViewMapper.toView(user);
        User accessorUser = userService.getUser();
        log.info("Deleted user with username {} by user with username {}", user.getUsername(), accessorUser.getUsername());
        return userView;
    }

    public UserView getUser(int id) {
        User user = userService.getUserById(id);
        checkResellerUserAccess(user);
        return userService.getUserFullView(user);
    }

    public UserView getUserByEmail(String email) {
        User user = userService.getUserByEmail(email);
        checkResellerUserAccess(user);
        return userService.getUserFullView(user);
    }

    public UserView getUserByUsername(String username) {
        User user = userService.getUserByUsername(username);
        checkResellerUserAccess(user);
        return userService.getUserFullView(user);
    }

    public UserView getUserById(Integer id) {
        User user = userService.getUserById(id);
        checkResellerUserAccess(user);
        return userService.getUserFullView(user);
    }

    public Page<UserView> getUsers(int page, int size) {
        User accessorUser = userService.getUser();
        Reseller reseller = accessorUser.getReseller();
        Role accessorRole = accessorUser.getRole();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());

        Page<User> queryResult;
        if (accessorRole.getName() == RoleName.ADMIN) {
            queryResult = userRepository.findAll(pageable);
        } else {
            queryResult = userRepository.findAllByReseller(reseller, pageable);
        }

        return queryResult.map(userViewMapper::toView);
    }

    public Page<UserView> getExpiredUsers(int page, int size) {
        User accessorUser = userService.getUser();
        Reseller reseller = accessorUser.getReseller();
        Role accessorRole = accessorUser.getRole();
        Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT));
        LocalDateTime dateTime = LocalDateTime.now();

        Page<User> queryResult;
        if (accessorRole.getName() == RoleName.ADMIN) {
            queryResult = userRepository.findAllExpiredUsers(dateTime, pageable);
        } else {
            queryResult = userRepository.findAllResellerExpiredUsers(reseller, dateTime, pageable);
        }

        return queryResult.map(userViewMapper::toView);
    }

    public void checkResellerUserAccess(User user) {
        User accessorUser = userService.getUser();
        Reseller reseller = accessorUser.getReseller();
        Role accessorRole = accessorUser.getRole();
        if (accessorRole.getName() != RoleName.ADMIN && user.getReseller().getId() != reseller
                .getId()) {
            throw new AccessDeniedException("Can't access user");
        }
    }
}
