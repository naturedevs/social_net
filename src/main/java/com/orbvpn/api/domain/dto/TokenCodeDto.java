package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TokenCodeDto {

    private int userId;
    private int discountRate;
}
