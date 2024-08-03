package com.employed.bar.adapters.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ConsumptionDto {
    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDateTime date;

    @NotNull
    @Positive
    private BigDecimal amount;


}

