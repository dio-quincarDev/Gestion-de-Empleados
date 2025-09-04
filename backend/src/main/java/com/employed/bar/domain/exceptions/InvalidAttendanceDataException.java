package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidAttendanceDataException extends BaseDomainException {
    public InvalidAttendanceDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_ATTENDANCE_DATA");
    }
}
