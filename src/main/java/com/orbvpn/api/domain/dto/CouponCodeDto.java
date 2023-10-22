package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CouponCodeDto {

    private String couponCode;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private int discountRate;
    private int quantity;
}
