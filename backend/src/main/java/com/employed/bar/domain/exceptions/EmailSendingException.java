package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public class EmailSendingException extends BaseDomainException {
    public EmailSendingException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL_SENDING_FAILED", cause);
    }
}
