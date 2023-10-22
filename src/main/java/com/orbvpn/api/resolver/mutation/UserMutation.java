package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.config.security.Unsecured;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.dto.UserProfileEdit;
import com.orbvpn.api.domain.dto.UserProfileView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserSubscription;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.reposiitory.UserRepository;
import com.orbvpn.api.service.GroupService;
import com.orbvpn.api.service.UserService;
import com.orbvpn.api.service.UserSubscriptionService;
import com.orbvpn.api.service.notification.NotificationService;
import com.orbvpn.api.utils.Utilities;

import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.orbvpn.api.domain.ValidationProperties.BAD_PASSWORD_MESSAGE;
import static com.orbvpn.api.domain.ValidationProperties.PASSWORD_PATTERN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
@Validated
public class UserMutation implements GraphQLMutationResolver {

    private final UserService userService;
    private final UserSubscriptionService userSubscriptionService;
    private final GroupService groupService;
    private final UserViewMapper userViewMapper;
    private final NotificationService notificationService;

    @Unsecured
    public AuthenticatedUser register(@Valid UserCreate userCreate) {
        return userService.register(userCreate);
    }

    @Unsecured
    public AuthenticatedUser signup(@Email String email, @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE) String password, String referral) {
        return userService.register(email, password, referral);
    }

    @Unsecured
    public AuthenticatedUser login(@Email String email, @NotBlank String password) {
        return userService.login(email, password);
    }

    @Unsecured
    public boolean requestResetPassword(@Email String email) {
        return userService.requestResetPassword(email);
    }

    @Unsecured
    public boolean resetPassword(@NotBlank String token,
                                 @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE) String password) {
        return userService.resetPassword(token, password);
    }

    public boolean changePassword(String oldPassword,
                                  @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE) String password) {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int id = userDetails.getId();
        return userService.changePassword(id, oldPassword, password);
    }

    public UserProfileView editProfile(UserProfileEdit userProfile) {
        return userService.editProfile(userProfile);
    }

    public Boolean editProfileByAdmin(int id, UserCreate updatedUser, UserProfileEdit updatedProfile) {
        User user = userService.getUserById(id);
        
        userService.updateUser(user, updatedUser);
        
        userService.editProfileByAdmin(user, updatedProfile);

        return true;
    }

    public Boolean editAutoRenew(Boolean isActive) {
        return userService.editAutoRenew(isActive);
    }

//////// ADMIN FUNCTION //////////////////

    @RolesAllowed(ADMIN)
    public boolean createUser(UserCreate user, UserProfileEdit userProfile) {

        User createdUser = userService.createUser(user);

        userService.editProfileByAdmin(createdUser, userProfile);

        return true;
    }

    @RolesAllowed(ADMIN)
    public boolean deleteBundleUsers(int[] ids) {
        for (int id : ids) {
            userService.deleteUser(userService.getUserById(id));
        }
        return true;
    }

    @RolesAllowed(ADMIN)
    public UserView createNewUserByAdmin(int groupId, int resellerId, String firstName, String lastName, String userName, String email, String devices, String country, String phone) {

        // 1. create user
        // 1A) generate random password
        var randomPassword = Utilities.getRandomPassword(10);

        // 1B) register new user
        var user = userService.createUserByAdmin(resellerId, email, userName, randomPassword);

        // 2. profile
        var userProfile = UserProfileEdit.builder()
            .firstName(firstName)
            .lastName(lastName)
            .country(country)
            .phone(phone)
            .build();
        userService.editProfileByAdmin(user, userProfile);

        // group
        var group = groupService.getById(groupId);
        var subscription = userSubscriptionService.createSubscriptionByAdmin(user, group);
        var userView = userViewMapper.toView(user);

        // send welcome email
        notificationService.welcomingNewUsers(user, subscription, randomPassword);

        return userView;
    }

    @RolesAllowed(ADMIN)
    public boolean deleteUser(int id) {
        userService.deleteUser(userService.getUserById(id));
        return true;
    }

    @RolesAllowed(ADMIN)
    public boolean updateSubscription(int userId, int multiLoginCount, BigInteger dailyBandwidth, BigInteger downloadUpload) {

        UserSubscription userSubscription = userSubscriptionService.getCurrentSubscription(userService.getUserById(userId));
        userSubscription.setMultiLoginCount(multiLoginCount);
        userSubscription.setDailyBandwidth(dailyBandwidth);
        userSubscription.setDownloadUpload(downloadUpload);
        userSubscriptionService.save(userSubscription);

        return true;
    }

}
