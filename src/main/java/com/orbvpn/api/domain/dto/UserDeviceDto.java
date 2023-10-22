package com.orbvpn.api.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDeviceDto {
    private String os;
    private String deviceId;
    private String appVersion;
    private String deviceName;
    private String deviceModel;
    private String fcmToken;
}
