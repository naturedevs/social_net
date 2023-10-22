package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacebookDeleteRequestResponse {
  private String url;

  @JsonProperty("confirmation_code")
  private String confirmationCode;
}
