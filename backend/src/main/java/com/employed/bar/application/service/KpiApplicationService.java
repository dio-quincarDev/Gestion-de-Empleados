package com.employed.bar.application.service;

import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.kpi.EmployeeKpiSummary;
import com.employed.bar.domain.model.kpi.ManagerKpis;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.KpiServicePort;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KpiApplicationService implements KpiServicePort {

    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepositoryPort attendanceRepository;
    private final ConsumptionRepository consumptionRepository;

    @Override
    public ManagerKpis getManagerKpis(LocalDate startDate, LocalDate endDate) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();

        // KPI 1: Total Active vs. Inactive Employees
        long totalActiveEmployees = allEmployees.stream().filter(e -> e.getStatus() == EmployeeStatus.ACTIVE).count();
        long totalInactiveEmployees = allEmployees.stream().filter(e -> e.getStatus() == EmployeeStatus.INACTIVE).count();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Calculate KPIs per employee for sorting
        List<EmployeeKpiSummary> employeeKpiSummaries = allEmployees.stream()
                .map(employee -> {
                    List<AttendanceRecordClass> attendanceRecords = attendanceRepository.findByEmployeeAndDateRange(employee, startDate, endDate);
                    double totalHoursWorked = attendanceRecords.stream()
                            .mapToDouble(record -> {
                                if (record.getEntryTime() != null && record.getExitTime() != null) {
                                    return java.time.Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes() / 60.0;
                                }
                                return 0;
                            })
                            .sum();

                    List<ConsumptionClass> consumptions = consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDateTime, endDateTime, null);
                    BigDecimal totalConsumptions = consumptions.stream()
                            .map(ConsumptionClass::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new EmployeeKpiSummary(
                            employee.getId(),
                            employee.getName(),
                            totalHoursWorked,
                            totalConsumptions
                    );
                })
                .collect(Collectors.toList());

        // KPI 2: Total Hours Worked (Overall)
        double totalHoursWorkedOverall = employeeKpiSummaries.stream().mapToDouble(EmployeeKpiSummary::getTotalHoursWorked).sum();

        // KPI 3: Total Consumptions (Overall)
        BigDecimal totalConsumptionsOverall = employeeKpiSummaries.stream().map(EmployeeKpiSummary::getTotalConsumptions).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Top N Employees by Hours Worked (e.g., Top 5)
        List<EmployeeKpiSummary> topEmployeesByHoursWorked = employeeKpiSummaries.stream()
                .sorted(Comparator.comparingDouble(EmployeeKpiSummary::getTotalHoursWorked).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Top N Employees by Consumptions (e.g., Top 5)
        List<EmployeeKpiSummary> topEmployeesByConsumptions = employeeKpiSummaries.stream()
                .sorted(Comparator.comparing(EmployeeKpiSummary::getTotalConsumptions).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return new ManagerKpis(
                totalActiveEmployees,
                totalInactiveEmployees,
                totalHoursWorkedOverall,
                totalConsumptionsOverall,
                topEmployeesByHoursWorked,
                topEmployeesByConsumptions
        );
    }
}
