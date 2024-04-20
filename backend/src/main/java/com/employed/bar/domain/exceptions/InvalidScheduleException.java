package com.employed.bar.domain.exceptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
public class InvalidScheduleException extends RuntimeException {
    public InvalidScheduleException(String message){
        super(message);
    }
}
