package com.orbvpn.api.exception;

public class TokenCodeAlreadyUsedException extends RuntimeException {
    public TokenCodeAlreadyUsedException(String code) {
        super(String.format("Token code '%s' is already used.",code));
    }
}
