package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.UserDeviceView;
import com.orbvpn.api.service.UserDeviceService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.RESELLER;

@Component
@RequiredArgsConstructor
public class UserDeviceQuery implements GraphQLQueryResolver {

    private final UserDeviceService userDeviceService;

    public List<UserDeviceView> getActiveDevices() {
        return userDeviceService.getActiveDevices();
    }

    @RolesAllowed({ADMIN, RESELLER})
    public List<UserDeviceView> resellerGetActiveDevices(int userId) {
        return userDeviceService.resellerGetActiveDevices(userId);
    }
}
