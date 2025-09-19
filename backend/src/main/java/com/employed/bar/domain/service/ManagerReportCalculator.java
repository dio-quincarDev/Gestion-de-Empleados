package com.employed.bar.domain.service;

import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.ReportTotals;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerReportCalculator {

    public ManagerReport calculate(List<EmployeeClass> employees, List<Report> reports) {
        List<EmployeeSummary> employeeSummaries = employees.stream()
                .map(employee -> {
                    Report report = reports.stream()
                            .filter(r -> r.getEmployeeId().equals(employee.getId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Report not found for employee: " + employee.getId()));

                    BigDecimal totalEarnings = report.getTotalEarnings();
                    BigDecimal totalConsumptions = report.getTotalConsumptionAmount();
                    BigDecimal netPay = totalEarnings.subtract(totalConsumptions);

                    return new EmployeeSummary(
                            employee.getName(),
                            report.getTotalAttendanceHours(),
                            totalEarnings,
                            totalConsumptions,
                            netPay
                    );
                })
                .collect(Collectors.toList());

        double totalRegularHoursWorked = employeeSummaries.stream().mapToDouble(EmployeeSummary::getTotalHoursWorked).sum();
        BigDecimal totalEarnings = employeeSummaries.stream().map(EmployeeSummary::getTotalEarnings).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalConsumptions = employeeSummaries.stream().map(EmployeeSummary::getTotalConsumptions).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalNetPay = employeeSummaries.stream().map(EmployeeSummary::getNetPay).reduce(BigDecimal.ZERO, BigDecimal::add);

        ReportTotals totals = new ReportTotals(totalRegularHoursWorked, 0, totalEarnings, totalConsumptions, totalNetPay);

        return new ManagerReport(employeeSummaries, totals);
    }
}
