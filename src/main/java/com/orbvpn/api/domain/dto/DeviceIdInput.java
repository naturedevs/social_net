package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *  UDID for iOS, Serial number for Android, Name for MacOS and Windows
 */
@Getter
@Setter
@ToString
public class DeviceIdInput {
    private String uuid;
    private String serial;
    private String name;

    public String getValue(){
        if(uuid != null)
            return uuid;
        else if(serial != null)
            return serial;
        if(name != null)
            return name;
        throw new RuntimeException("non of device id parts have value.");
    }
}
