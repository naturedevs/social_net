package com.orbvpn.api.exception;

import com.orbvpn.api.config.Messages;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(Exception exception) {
        super(Messages.getMessage("invalid_credentials"));
    }

}
