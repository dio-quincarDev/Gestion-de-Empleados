package com.employed.bar.domain.model.report;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class HoursCalculation {
    BigDecimal totalHours;
    BigDecimal regularHours;
    BigDecimal overtimeHours;
}
