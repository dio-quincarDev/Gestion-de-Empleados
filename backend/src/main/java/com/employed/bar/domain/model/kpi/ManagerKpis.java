package com.employed.bar.domain.model.kpi;

import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
public class ManagerKpis {
    long totalActiveEmployees;
    long totalInactiveEmployees;
    double totalHoursWorkedOverall;
    BigDecimal totalConsumptionsOverall;
    List<EmployeeKpiSummary> topEmployeesByHoursWorked;
    List<EmployeeKpiSummary> topEmployeesByConsumptions;
}
