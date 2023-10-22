package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmNotificationDto {

    private Boolean status;
    private String message;

    public FcmNotificationDto() {
        this.status = true;
        this.message = "Success";
    }

    public FcmNotificationDto(String message) {
        this.status = false;
        this.message = message;
    }

    public FcmNotificationDto(int notificationCount) {
        this.status = true;
        this.message = String.format("Notification sent to %d users.", notificationCount);
    }
}
