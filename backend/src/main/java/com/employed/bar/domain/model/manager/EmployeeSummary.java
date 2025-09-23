package com.employed.bar.domain.model.manager;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class EmployeeSummary {
    String employeeName;
    double totalHoursWorked;
    BigDecimal totalEarnings;
    BigDecimal totalConsumptions;
    BigDecimal netPay;
}
