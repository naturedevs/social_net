package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParspalCreatePaymentResponse {
  private String payment_id;
  private String link;
  private String status;
  private String message;
  private String error_type;
  private String error_code;
}
