package com.employed.bar.infrastructure.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateHourlyRateRequest {
    @NotNull(message = "Hourly rate is required")
    @Positive(message = "Hourly rate must be positive")
    private BigDecimal hourlyRate;
}
