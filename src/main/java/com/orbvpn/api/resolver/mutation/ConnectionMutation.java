package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.DeviceIdInput;
import com.orbvpn.api.service.ConnectionService;
import com.orbvpn.api.service.DeviceService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConnectionMutation implements GraphQLMutationResolver {
    private final ConnectionService connectionService;
    private final DeviceService deviceService;

    public Boolean disconnectBySessionId(String onlineSessionId) {
        return connectionService.disconnect(onlineSessionId);
    }

    public Boolean disconnectByUserIdAndDeviceId(Integer userId, DeviceIdInput deviceIdInput) {
        return connectionService.disconnect(userId, deviceIdInput);
    }

    public Boolean activateDevice(Integer userId, DeviceIdInput deviceIdInput) {
        return deviceService.activateDevice(userId, deviceIdInput);
    }

    public Boolean deactivateDevice(Integer userId, DeviceIdInput deviceIdInput) {
        return deviceService.deactivateDevice(userId, deviceIdInput);
    }
}
