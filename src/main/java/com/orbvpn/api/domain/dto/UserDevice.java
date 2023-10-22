package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * this DTO is just used for internal usage
 */
@Getter
@Setter
@ToString
public class UserDevice {
    private String username;
    private String deviceId;
}
