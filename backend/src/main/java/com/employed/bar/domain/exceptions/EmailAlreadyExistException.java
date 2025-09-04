package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistException extends BaseDomainException {
    public EmailAlreadyExistException(String message){
        super(message, HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
    }
}
