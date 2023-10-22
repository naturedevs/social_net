package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParspalCreatePaymentRequest {
  private double amount;
  private String returnUrl;
  private String orderId;
  private String currency = "usd";
}
