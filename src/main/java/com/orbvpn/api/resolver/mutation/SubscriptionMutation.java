package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.service.GroupService;
import com.orbvpn.api.service.RenewUserSubscriptionService;
import com.orbvpn.api.service.UserService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.RESELLER;

@Component
@RequiredArgsConstructor
@Validated
public class SubscriptionMutation implements GraphQLMutationResolver {

    private final RenewUserSubscriptionService renewSubscriptionService;
    private final UserService userService;
    private final GroupService groupService;

    @RolesAllowed(ADMIN)
    public UserSubscriptionView renewWithoutGroup(String username, int day) {

        User user = userService.getUserByUsername(username);
        return renewSubscriptionService.renewWithDayCount(user, day);
    }

    @RolesAllowed(ADMIN)
    public UserSubscriptionView resetUserSubscriptionWithCurrentGroup(String username) {

        User user = userService.getUserByUsername(username);
        return renewSubscriptionService.resetUserSubscription(user);
    }

    @RolesAllowed(ADMIN)
    public UserSubscriptionView resetUserSubscriptionWithNewGroup(String username, int groupId) {

        User user = userService.getUserByUsername(username);
        return renewSubscriptionService.resetUserSubscription(user, groupId);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public UserSubscriptionView resellerRenewUserSubscriptionWithCurrentGroup(String username) {

        User user = userService.getUserByUsername(username);
        return renewSubscriptionService.resellerRenewUserSubscription(user);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public UserSubscriptionView resellerRenewUserSubscriptionWithNewGroup(String username, int groupId) {

        User user = userService.getUserByUsername(username);
        Group group = groupService.getById(groupId);
        return renewSubscriptionService.resellerRenewUserSubscription(user, group);
    }

}
