package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmployeeNotFoundException extends BaseDomainException {
    public EmployeeNotFoundException(String message){
        super(message, HttpStatus.NOT_FOUND, "EMPLOYEE_NOT_FOUND");
    }
}
