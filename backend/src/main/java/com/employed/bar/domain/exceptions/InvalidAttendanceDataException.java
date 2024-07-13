package com.employed.bar.domain.exceptions;

public class InvalidAttendanceDataException extends RuntimeException {
    public InvalidAttendanceDataException(String message) {
        super(message);
    }
}
