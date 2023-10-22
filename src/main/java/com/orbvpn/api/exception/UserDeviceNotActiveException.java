package com.orbvpn.api.exception;

public class UserDeviceNotActiveException extends RuntimeException {

    public UserDeviceNotActiveException(String deviceId) {
        super(String.format("User Device with the device id of %s is not active!", deviceId));
    }

    public UserDeviceNotActiveException(Long userDeviceId) {
        super(String.format("User Device with the id of %d is not active!", userDeviceId));
    }
}