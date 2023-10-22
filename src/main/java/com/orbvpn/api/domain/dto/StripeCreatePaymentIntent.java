package com.orbvpn.api.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StripeCreatePaymentIntent {
  @NotBlank
  private String tokenId;

  @Positive
  private Integer groupId;
}
