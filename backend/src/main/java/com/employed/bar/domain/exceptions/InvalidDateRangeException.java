package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidDateRangeException extends BaseDomainException {
    public InvalidDateRangeException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_DATE_RANGE");
    }
}
