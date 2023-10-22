package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripePaymentResponse {
  private String clientSecret;
  private String paymentIntentId;
  private Boolean requiresAction;
  private String error;
}
