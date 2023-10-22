package com.orbvpn.api.service;

import com.orbvpn.api.config.Messages;
import com.orbvpn.api.config.security.JwtTokenUtil;
import com.orbvpn.api.domain.dto.*;
import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.domain.enums.GatewayName;
import com.orbvpn.api.domain.enums.PaymentCategory;
import com.orbvpn.api.domain.enums.PaymentStatus;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.BadCredentialsException;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.*;
import com.orbvpn.api.reposiitory.*;
import com.orbvpn.api.service.notification.NotificationService;
import com.orbvpn.api.service.payment.PaymentService;
import com.orbvpn.api.service.reseller.ResellerService;
import com.orbvpn.api.utils.Utilities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;
    private final UserViewMapper userViewMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    private final PasswordResetRepository passwordResetRepository;
    private final PaymentRepository paymentRepository;

    private final UserProfileRepository userProfileRepository;
    private final UserProfileEditMapper userProfileEditMapper;
    private final UserProfileViewMapper userProfileViewMapper;
    private final UserSubscriptionViewMapper userSubscriptionViewMapper;
    private final ReferralCodeRepository referralCodeRepository;

    private final RoleService roleService;
    private final ResellerService resellerService;
    private final GroupService groupService;
    private final UserSubscriptionService userSubscriptionService;
    private final RadiusService radiusService;
    private final PasswordService passwordService;
    private final PaymentService paymentService;

    private final NotificationService notificationService;

    @PostConstruct
    public void init() {
        paymentService.setUserService(this);
    }

    public AuthenticatedUser register(UserCreate userCreate) {
        return register(userCreate.getEmail(), userCreate.getPassword(), null);
    }

    public AuthenticatedUser register(String email, String password, String referral) {
        log.info("Creating user with data {}", email);

        Optional<User> userEntityOptional = userRepository.findByEmail(email);
        if (userEntityOptional.isPresent()) {
            throw new BadRequestException(Messages.getMessage("email_exists"));
        }

        UserCreate userCreate = new UserCreate();
        userCreate.setEmail(email);
        userCreate.setPassword(password);

        User user = userCreateMapper.createEntity(userCreate);
        user.setUsername(userCreate.getEmail());
        passwordService.setPassword(user, userCreate.getPassword());
        Role role = roleService.getByName(RoleName.USER);
        user.setRole(role);
        user.setReseller(resellerService.getOwnerReseller());
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);

        if (referral != null && !referral.isEmpty()) {
            ReferralCode referralCode = referralCodeRepository.findReferralCodeByCode(referral);
            if (referralCode != null)
                referralCode.setInvitations(referralCode.getInvitations() + 1);
        }

        assignTrialSubscription(user);

        UserView userView = userViewMapper.toView(user);
        log.info("Created user {}", userView);
        return loginInfo(user);
    }

    public List<User> createBulkUser(BulkUserCreate usersToCreate) {
        List<User> createdUsers = new ArrayList<>();

        Role role = roleService.getByName(RoleName.USER);
        List<User> users = usersToCreate.getUsers();
        List<UserProfile> profiles = usersToCreate.getProfiles();
        List<BulkSubscription> subscriptions = usersToCreate.getSubscriptions();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            UserProfile profile = profiles.get(i);
            BulkSubscription subscription = subscriptions.get(i);

            Optional<User> userEntityOptional = userRepository.findByEmail(user.getEmail());
            if (userEntityOptional.isPresent())
                continue;

            user.setUsername(user.getEmail());
            String password = user.getPassword();
            if (password == null || password.isEmpty())
                password = Utilities.getRandomPassword(10);

            passwordService.setPassword(user, password);
            user.setRole(role);
            user.setReseller(resellerService.getOwnerReseller());
            profile.setUser(user);
            user.setProfile(profile);

            userSubscriptionService.createBulkSubscription(user, subscription);
            userProfileRepository.save(profile);
            createdUsers.add(userRepository.save(user));
        }

        return createdUsers;
    }

    public User createUser(UserCreate userCreate) {

        Optional<User> oldUser = userRepository.findByEmail(userCreate.getEmail());
        if (oldUser.isPresent()) {
            throw new BadRequestException(Messages.getMessage("email_exists"));
        }

        User user = new User();

        return userRepository.save(setUserInfo(user, userCreate));
    }

    public User updateUser(User user, UserCreate userCreate) {
        if (!user.getEmail().equals(userCreate.getEmail())) {
            Optional<User> oldUser = userRepository.findByEmail(userCreate.getEmail());
            if (oldUser.isPresent()) {
                throw new BadRequestException(Messages.getMessage("email_exists"));
            }
        }

        User newUser = userRepository.save(setUserInfo(user, userCreate));

        // // send message to current user about password info
        // notificationService.resetPasswordDoneByAdmin(user, userCreate.getPassword());

        // if (!user.getEmail().equals(userCreate.getEmail())) {
        //     // send message to current user that current mail was changed
        //     notificationService.resetEmailByAdmin(user, newUser.getEmail());
        // }

        return newUser;
    }

    public User setUserInfo(User user, UserCreate updatedInfo) {

        if (updatedInfo.getPassword() == null || updatedInfo.getPassword().isEmpty()) {
            String password = Utilities.getRandomPassword(10);
            updatedInfo.setPassword(password);
        }

        user.setEmail(updatedInfo.getEmail());
        user.setPassword(updatedInfo.getPassword());

        passwordService.setPassword(user, user.getPassword());
        user.setUsername(updatedInfo.getEmail());
        if (updatedInfo.getResellerId() != null) {
            user.setReseller(resellerService.getResellerById(updatedInfo.getResellerId()));
        }
        user.setRole(roleService.getByName(RoleName.USER));

        return user;
    }

    public User createUserByAdmin(int resellerId, String email, String username, String password) {
        Optional<User> userEntityOptional = userRepository.findByEmail(email);
        if (userEntityOptional.isPresent()) {
            throw new BadRequestException(Messages.getMessage("email_exists"));
        }

        var user = new User();
        user.setEmail(email);
        user.setPassword(password);
        passwordService.setPassword(user, password);
        user.setUsername(username);
        if (resellerId != 0) {
            var reseller = resellerService.getResellerById(resellerId);
            user.setReseller(reseller);
        }
        user.setRole(roleService.getByName(RoleName.USER));
        var profile = new UserProfile();
        profile.setUser(user);
        user.setProfile(profile);

        notificationService.welcomingNewUsersCreatedByAdmin(user, password);
        return userRepository.save(user);
    }

    public void assignTrialSubscription(User user) {
        // Assign trial group
        Group group = groupService.getById(1);
        String paymentId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .user(user)
                .status(PaymentStatus.PENDING)
                .gateway(GatewayName.FREE)
                .category(PaymentCategory.GROUP)
                .price(group.getPrice())
                .groupId(group.getId())
                .paymentId(paymentId)
                .build();
        paymentRepository.save(payment);

        paymentService.fullFillPayment(payment);
    }

    public String generateRandomString() {
        int length = 10;
        return RandomStringUtils.random(length, true, true);
    }

    public AuthenticatedUser login(String email, String password) {
        log.info("Authentication user with email {}", email);

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception ex) {
            throw new BadCredentialsException(ex);
        }

        User user = (User) authentication.getPrincipal();
        return loginInfo(user);
    }

    public AuthenticatedUser loginInfo(User user) {
        UserView userView = userViewMapper.toView(user);
        String token = jwtTokenUtil.generateAccessToken(user);

        return new AuthenticatedUser(token, userView);
    }

    public boolean requestResetPassword(String email) {
        log.info("Resetting password for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with specified email not exists"));

        String token = generateRandomString();

        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setUser(user);
        passwordReset.setToken(token);
        passwordResetRepository.save(passwordReset);

        notificationService.resetPassword(user, token);
        passwordResetRepository.deleteByUserAndTokenNot(user, token);
        return true;
    }

    public boolean resetPassword(String token, String password) {

        PasswordReset passwordReset = passwordResetRepository.findById(token).orElseThrow(() ->
                new NotFoundException("Token was not found"));

        User user = passwordReset.getUser();
        passwordService.setPassword(user, password);
        userRepository.save(user);
        passwordResetRepository.delete(passwordReset);
        notificationService.resetPasswordDone(user);
        return true;
    }

    public boolean changePassword(int id, String oldPassword, String password) {
        log.info("Changing password for user with id {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String oldPasswordEncoded = user.getPassword();
        if (!passwordEncoder.matches(oldPassword, oldPasswordEncoded)) {
            throw new BadRequestException("Wrong password");
        }

        passwordService.setPassword(user, password);
        userRepository.save(user);

        return true;
    }

    /**
     * Delete user and the whole dependant entities including:
     * userProfile, reseller, resellerAddCredit, userSubscription, PasswordReset, Payment, radaacct, radcheck
     * <p>
     * these not finalized entities are not checked yet : StripeCustomer, Ticket, MoreLoginCount, OathToken, TicketReply
     *
     * @param user: user for deletion
     */
    public void deleteUser(User user) {
        radiusService.deleteUserRadChecks(user);
        radiusService.deleteUserRadAcct(user);
        userRepository.delete(user);
        try {
            resellerService.deleteAllByUser(user);
        } catch (Exception e) {
        }
    }

    public User deleteOauthUser(String oauthId) {
        User user = userRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        deleteUser(user);

        return user;
    }

    public UserProfileView editProfile(UserProfileEdit userProfileEdit) {
        User user = getUser();

        log.info("Editing user{} profile{}", user.getId(), userProfileEdit);

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());

        UserProfile edited = userProfileEditMapper.edit(userProfile, userProfileEdit);
        edited.setUser(user);

        userProfileRepository.save(edited);

        return userProfileViewMapper.toView(edited);
    }

    public UserProfileView editProfileByAdmin(User user, UserProfileEdit userProfileEdit) {
        log.info("Editing user{} profile{}", user.getId(), userProfileEdit);
        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());
        UserProfile edited = userProfileEditMapper.edit(userProfile, userProfileEdit);
        edited.setUser(user);
        userProfileRepository.save(edited);
        return userProfileViewMapper.toView(edited);
    }

    public UserProfileView getProfile() {
        User user = getUser();

        UserProfile userProfile = userProfileRepository.findByUser(user).orElse(new UserProfile());

        return userProfileViewMapper.toView(userProfile);
    }

    public UserSubscriptionView getUserSubscription() {
        User user = getUser();
        UserSubscription currentSubscription = userSubscriptionService.getCurrentSubscription(user);
        return userSubscriptionViewMapper.toView(currentSubscription);
    }

    public UserSubscriptionView getUserSubscription(User user) {
        UserSubscription currentSubscription = userSubscriptionService.getCurrentSubscription(user);
        return userSubscriptionViewMapper.toView(currentSubscription);
    }

    public List<UserDeviceInfo> getUserDeviceInfo() {
        User user = getUser();
        String username = user.getUsername();

        List<String> allDevices = userRepository.findAllUserDevices(username);
        List<String> allActiveDevices = userRepository.findAllActiveUserDevices(username);

        return allDevices.stream()
                .filter(StringUtils::isNoneBlank)
                .map(s -> {
                    UserDeviceInfo userDeviceInfo = new UserDeviceInfo();
                    userDeviceInfo.setName(s);
                    userDeviceInfo.setActive(allActiveDevices.contains(s));
                    return userDeviceInfo;
                }).collect(Collectors.toList());
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(User.class, email));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(User.class, username));
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public Role getUserRole() {
        User user = getUser();
        return user.getRole();
    }

    public boolean isAdmin() {
        User user = getUser();
        return user.getRole().getName() == RoleName.ADMIN;
    }

    public UserView getUserView() {
        User user = getUser();

        return getUserFullView(user);
    }

    public UserView getUserFullView(User user) {
        UserSubscription subscription = userSubscriptionService.getCurrentSubscription(user);
        user.setSubscription(subscription);

        UserView userView = userViewMapper.toView(user);
        userView.setUserDevicesInfo(getUserDeviceInfo());
        return userView;
    }

    public Boolean editAutoRenew(Boolean isActive) {
        User user = getUser();
        user.setAutoRenew(isActive);
        userRepository.save(user);

        return true;
    }

    public int getActiveUserCountOfReseller(int resellerId) {
        return userRepository.getActiveUsersOfReseller(resellerId);
    }


}
