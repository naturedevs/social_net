package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppStoreVerifyReceiptRequest {

  @JsonProperty("receipt-data")
  private String receiptData;

  private String password;
}
