package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.ConnectionHistoryView;
import com.orbvpn.api.domain.dto.DeviceView;
import com.orbvpn.api.domain.dto.OnlineSessionView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.ConnectionService;
import com.orbvpn.api.service.DeviceService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConnectionQuery implements GraphQLQueryResolver {
    private final ConnectionService connectionService;
    private final DeviceService deviceService;

    public List<ConnectionHistoryView> getConnectionHistory(Integer userId) {
        return connectionService.getConnectionHistory(userId);
    }

    public List<OnlineSessionView> getOnlineSessions(Integer userId) {
        return connectionService.getOnlineSessions(userId);
    }

    public Page<UserView> getOnlineUsers(Integer page, Integer size, Integer serverId, Integer groupId, Integer roleId, Integer serviceGroupId) {
        return connectionService.getOnlineUsers(page, size, serverId, groupId, roleId, serviceGroupId);
    }

    public List<DeviceView> getDevices(Integer userId) {
        return deviceService.getDevices(userId);
    }

    public List<DeviceView> getDevicesByEmail(String email) {
        return deviceService.getDevicesByEmail(email);
    }
}