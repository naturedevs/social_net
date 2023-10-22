package com.orbvpn.api.exception;


public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Class<?> clazz, long id) {
    super(String.format("Entity %s with composite id %d not found", clazz.getSimpleName(), id));
  }

  public NotFoundException(Class<?> clazz, Object id) {
    super(String.format("Entity %s with composite id %s not found", clazz.getSimpleName(), id.toString()));
  }
}
