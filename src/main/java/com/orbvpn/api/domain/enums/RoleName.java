package com.orbvpn.api.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum RoleName implements GrantedAuthority {
  USER(Constants.USER), ADMIN(Constants.ADMIN), RESELLER(Constants.RESELLER);

  private final String authority;

  public static class Constants {

    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";
    public static final String RESELLER = "RESELLER";
  }

}