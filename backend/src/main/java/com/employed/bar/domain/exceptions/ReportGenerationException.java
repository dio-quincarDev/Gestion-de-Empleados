package com.employed.bar.domain.exceptions;

import org.springframework.http.HttpStatus;


public class ReportGenerationException extends BaseDomainException {
    public ReportGenerationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "REPORT_GENERATION_FAILED");
    }
}
