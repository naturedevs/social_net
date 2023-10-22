package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaypalApprovePaymentResponse {
    private boolean success;
    private String errorMessage;
}