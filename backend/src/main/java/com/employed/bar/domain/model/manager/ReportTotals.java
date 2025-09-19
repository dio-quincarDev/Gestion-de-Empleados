package com.employed.bar.domain.model.manager;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class ReportTotals {
    double totalRegularHoursWorked;
    double totalOvertimeHoursWorked;
    BigDecimal totalEarnings;
    BigDecimal totalConsumptions;
    BigDecimal totalNetPay;
}
