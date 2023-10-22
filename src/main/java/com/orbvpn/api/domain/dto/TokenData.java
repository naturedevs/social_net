package com.orbvpn.api.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenData {
  private String email;
  private String oauthId;
  private long exp;
  private long iat;
}
