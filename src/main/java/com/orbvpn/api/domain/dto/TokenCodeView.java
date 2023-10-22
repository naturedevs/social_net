package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TokenCodeView {

    private long id;
    private String tokenCode;
    private long userId;
    private int discountRate;
    private boolean isActive;
    private LocalDateTime usedTimestamp;
    private LocalDateTime createdAt;
}
