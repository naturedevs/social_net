package com.orbvpn.api.exception;

public class UnauthenticatedAccessException extends RuntimeException {

  public UnauthenticatedAccessException(String message) {
    super(message);
  }
}
