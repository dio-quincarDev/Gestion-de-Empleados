package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseDomainException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;

    public BaseDomainException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public BaseDomainException(String message, HttpStatus httpStatus, String errorCode, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
