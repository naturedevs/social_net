package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.FcmNotificationDto;
import com.orbvpn.api.domain.dto.NotificationDto;
import com.orbvpn.api.domain.dto.UserDeviceDto;
import com.orbvpn.api.domain.dto.UserDeviceView;
import com.orbvpn.api.exception.BadRequestException;
import com.orbvpn.api.service.UserDeviceService;
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
public class UserDeviceMutation implements GraphQLMutationResolver {

    private final UserDeviceService userDeviceService;

    UserDeviceView loginDevice(UserDeviceDto userDeviceDto) {
        if(userDeviceDto.getDeviceId().length() == 0)
            throw new BadRequestException("DeviceId can not be empty string!");

        return userDeviceService.loginDevice(userDeviceDto);
    }

    UserDeviceView logoutDeviceByUserDeviceId(Long userDeviceId) {
        return userDeviceService.logoutDevice(userDeviceId);
    }

    UserDeviceView logoutDeviceByDeviceId(String deviceId) {
        if (deviceId.length() == 0)
            throw new BadRequestException("DeviceId can not be empty string!");

        return userDeviceService.logoutDevice(deviceId);
    }

    @RolesAllowed({ADMIN, RESELLER})
    UserDeviceView resellerLogoutDevice(int userId, String deviceId) {
        if (deviceId.length() == 0)
            throw new BadRequestException("DeviceId can not be empty string!");

        return userDeviceService.resellerLogoutDevice(userId, deviceId);
    }

    @RolesAllowed({ADMIN, RESELLER})
    UserDeviceView resellerDeactivateDevice(String deviceId) {
        return userDeviceService.blockDevice(deviceId);
    }

    @RolesAllowed({ADMIN, RESELLER})
    UserDeviceView resellerActivateDevice(String deviceId) {
        return userDeviceService.unblockDevice(deviceId);
    }

    @RolesAllowed(ADMIN)
    FcmNotificationDto sendNotificationByDeviceId(String deviceId, NotificationDto notificationDto) {
        return userDeviceService.sendNotificationByDeviceId(deviceId, notificationDto);
    }

    @RolesAllowed(ADMIN)
    FcmNotificationDto sendNotificationByToken(String token, NotificationDto notificationDto) {
        return userDeviceService.adminSendNotificationByToken(token, notificationDto);
    }

    @RolesAllowed(ADMIN)
    FcmNotificationDto sendNotificationToAll(NotificationDto notificationDto) {
        return userDeviceService.adminSendNotificationToAll(notificationDto);
    }

}
