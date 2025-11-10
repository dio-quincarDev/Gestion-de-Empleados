package com.employed.bar.domain.exceptions;

public class ConsumptionNotFoundException extends RuntimeException {
    public ConsumptionNotFoundException(String message) {
        super(message);
    }
}
