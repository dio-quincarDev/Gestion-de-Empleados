package com.employed.bar.domain.service;

import com.employed.bar.domain.model.manager.EmployeeSummary;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.manager.ReportTotals;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class ManagerReportCalculator {

    public ManagerReport calculate(List<EmployeeClass> employees, List<Report> reports) {
        List<EmployeeSummary> employeeSummaries = employees.stream()
                .map(employee -> {
                    // Intenta encontrar el reporte individual para el empleado
                    Report report = reports.stream()
                            .filter(r -> r.getEmployeeId().equals(employee.getId()))
                            .findFirst()
                            .orElse(null); // Si no se encuentra, devuelve null

                    if (report == null) {
                        return null; // Si no hay reporte, este empleado no se incluirá en el resumen
                    }

                    BigDecimal totalEarnings = report.getTotalEarnings();
                    BigDecimal totalConsumptions = report.getTotalConsumptionAmount();
                    BigDecimal netPay = totalEarnings.subtract(totalConsumptions);

                    return new EmployeeSummary(
                            employee.getName(),
                            report.getHoursCalculation().getTotalHours(), // Total hours (regular + overtime)
                            totalEarnings,
                            totalConsumptions,
                            netPay,
                            employee.getPaymentMethod()
                    );
                })
                .filter(java.util.Objects::nonNull) // Filtra los EmployeeSummary nulos (empleados sin reporte)
                .collect(Collectors.toList());

        // Los cálculos de totales agregados deben basarse en los reportes individuales que SÍ existen
        // (la lista 'reports' ya está filtrada de nulos en ManagerReportApplicationService)
        BigDecimal aggregatedRegularHours = reports.stream()
                .map(report -> report.getHoursCalculation().getRegularHours())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal aggregatedOvertimeHours = reports.stream()
                .map(report -> report.getHoursCalculation().getOvertimeHours())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Los cálculos de totales desde employeeSummaries ya son correctos si los EmployeeSummary se crean solo para empleados con reporte
        BigDecimal totalEarnings = employeeSummaries.stream()
                .map(EmployeeSummary::getTotalEarnings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConsumptions = employeeSummaries.stream()
                .map(EmployeeSummary::getTotalConsumptions)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNetPay = employeeSummaries.stream()
                .map(EmployeeSummary::getNetPay)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReportTotals totals = new ReportTotals(
                aggregatedRegularHours,
                aggregatedOvertimeHours,
                totalEarnings,
                totalConsumptions,
                totalNetPay
        );

        return new ManagerReport(employeeSummaries, totals);
    }
}