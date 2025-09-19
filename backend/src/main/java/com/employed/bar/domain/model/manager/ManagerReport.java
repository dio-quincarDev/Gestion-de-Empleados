package com.employed.bar.domain.model.manager;

import lombok.Value;

import java.util.List;

@Value
public class ManagerReport {
    List<EmployeeSummary> employeeSummaries;
    ReportTotals totals;
}
