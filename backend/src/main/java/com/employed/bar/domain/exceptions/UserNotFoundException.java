package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseDomainException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, HttpStatus.NOT_FOUND, "USER_NOT_FOUND", cause);
    }
}
