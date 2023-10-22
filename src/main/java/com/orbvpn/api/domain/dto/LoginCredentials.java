package com.orbvpn.api.domain.dto;

import lombok.Data;

@Data
public class LoginCredentials {
  private String email;
  private String password;
}
