package com.orbvpn.api.domain.enums;

import lombok.Getter;

@Getter
public enum DeviceOsType {
    IOS ("iPhone"),
    ANDROID ("Android"),
    WINDOWS("Windows"),
    MACOS("Macos"),
    LINUX ("Linux");

    String externalAppAlternateName;

    DeviceOsType(String name){
        externalAppAlternateName = name;
    }
}
