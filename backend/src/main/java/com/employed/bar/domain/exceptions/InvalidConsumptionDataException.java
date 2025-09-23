package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidConsumptionDataException extends BaseDomainException {
    public InvalidConsumptionDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_CONSUMPTION_DATA");
    }
}
