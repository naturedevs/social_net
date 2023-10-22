package com.orbvpn.api.exception;

public class OauthLoginException extends RuntimeException {

  public OauthLoginException() {
    super("Oauth login failed");
  }

  public OauthLoginException(String message) {
    super(String.format("Oauth login failed: %s", message));
  }
}
