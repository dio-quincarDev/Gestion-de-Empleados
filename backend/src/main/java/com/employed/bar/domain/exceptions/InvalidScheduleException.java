package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;


public class InvalidScheduleException extends BaseDomainException {
    public InvalidScheduleException(String message){
        super(message, HttpStatus.BAD_REQUEST, "INVALID_SCHEDULE");
    }
}
