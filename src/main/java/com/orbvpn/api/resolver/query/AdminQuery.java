package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.UserViewMapper;
import com.orbvpn.api.service.AdminService;
import com.orbvpn.api.service.DeviceService;
import com.orbvpn.api.service.UserService;
import com.orbvpn.api.service.UserSubscriptionService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;

import javax.annotation.security.RolesAllowed;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;


@Component
@RequiredArgsConstructor
public class AdminQuery implements GraphQLQueryResolver {

    private final AdminService adminService;
    private final UserService userService;
    private final UserSubscriptionService userSubscriptionService;
    private final DeviceService deviceService;
    private final UserViewMapper userViewMapper;

    public Page<UserView> activeUsers(int page, int size) {
        return adminService.getActiveUsers(page, size);
    }

    public Page<UserView> inactiveUsers(int page, int size) {
        return adminService.getInactiveUsers(page, size);
    }

    public Page<UserView> allUsers(boolean sort, int page, int size, String param, String query) {
        return adminService.getAllUsers(sort, page, size, param, query);
    }

    public int totalActiveUsers() {
        return adminService.getTotalActiveUsers();
    }

    @RolesAllowed(ADMIN)
    public UserView getUserById(int id) {
        User user = userService.getUserById(id);
        user.setSubscription(userSubscriptionService.getCurrentSubscription(user));

        UserView userView = userViewMapper.toView(user);
        userView.setUserDeviceList(deviceService.getDevices(user.getId()));

        return userView;
    }

}
