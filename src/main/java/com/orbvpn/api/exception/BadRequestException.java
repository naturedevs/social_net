package com.orbvpn.api.exception;

public class BadRequestException extends RuntimeException {

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(Class<?> clazz, String message) {
    super(String
      .format("Wrong value submitted for class  %s with message %s", clazz.getSimpleName(),
        message));
  }

}
