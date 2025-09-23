package com.employed.bar.domain.model.kpi;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class EmployeeKpiSummary {
    Long employeeId;
    String employeeName;
    double totalHoursWorked;
    BigDecimal totalConsumptions;
}
