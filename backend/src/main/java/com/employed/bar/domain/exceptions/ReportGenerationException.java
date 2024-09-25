package com.employed.bar.domain.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReportGenerationException extends RuntimeException {
    public ReportGenerationException(String message) {
        super(message);
    }
}
