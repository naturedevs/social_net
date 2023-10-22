package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDeviceView {
    private Long id;
    private Long userId;
    private String os;
    private String deviceId;
    private LocalDateTime loginDate;
    private LocalDateTime logoutDate;
    private String appVersion;
    private String deviceModel;
    private String deviceName;
    private String fcmToken;
    private Boolean isActive;
    private Boolean isBlocked;
}
