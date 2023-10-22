package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParspalApprovePaymentRequest {
  private String receipt;
  private double amount;
  private String currency = "usd";
}
