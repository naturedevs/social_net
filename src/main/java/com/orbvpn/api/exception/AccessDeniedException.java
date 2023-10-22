package com.orbvpn.api.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(Class<?> clazz) {
        super(String.format("Access denied for resource %s", clazz.getSimpleName()));
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
