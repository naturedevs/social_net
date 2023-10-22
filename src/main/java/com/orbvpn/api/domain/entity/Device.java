package com.orbvpn.api.domain.entity;

import java.time.LocalDateTime;

public interface Device {
    String getDeviceInfo();

    LocalDateTime getLastConnectionStartTime();

    LocalDateTime getLastConnectionStopTime();

    String getLastSessionId();

    Integer getLastConnectedServerId();

    String getLastConnectedServerCountry();

    String getLastConnectedServerCity();
}
