package com.orbvpn.api.domain;

public class ValidationProperties {
  public static final String PASSWORD_PATTERN  = "^(?=.*[0-9])(?=.*[a-z]).{8,}$";
  public static final String BAD_PASSWORD_MESSAGE = "Password must at least contain 8 characters including letters and numbers";
}
