package com.employed.bar.application.service;

import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.kpi.EmployeeKpiSummary;
import com.employed.bar.domain.model.kpi.ManagerKpis;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.service.KpiServicePort;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KpiApplicationService implements KpiServicePort {

    private final EmployeeRepositoryPort employeeRepository;
    private final AttendanceRepositoryPort attendanceRepository;
    private final ConsumptionRepositoryPort consumptionRepositoryPort;

    public KpiApplicationService(EmployeeRepositoryPort employeeRepository, AttendanceRepositoryPort attendanceRepository, ConsumptionRepositoryPort consumptionRepositoryPort) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.consumptionRepositoryPort = consumptionRepositoryPort;
    }


    @Override
    public ManagerKpis getManagerKpis(LocalDate startDate, LocalDate endDate) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();

        long totalActiveEmployees = allEmployees.stream()
                .filter(e -> EmployeeStatus.ACTIVE.equals(e.getStatus()))
                .count();

        long totalInactiveEmployees = allEmployees.stream()
                .filter(e -> EmployeeStatus.INACTIVE.equals(e.getStatus()))
                .count();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<EmployeeKpiSummary> employeeKpiSummaries = allEmployees.stream()
                .filter(e -> EmployeeStatus.ACTIVE.equals(e.getStatus()))
                .map(employee -> {
                    List<AttendanceRecordClass> attendanceRecords =
                            attendanceRepository.findByEmployeeAndDateRange(employee, startDate, endDate);

                    double totalHoursWorked = attendanceRecords.stream()
                            .mapToDouble(record -> {
                                if (record.getEntryTime() != null && record.getExitTime() != null) {
                                    return Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes() / 60.0;
                                }
                                return 0;
                            })
                            .sum();

                    List<ConsumptionClass> consumptions =
                            consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(employee, startDateTime, endDateTime, null);

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

        double totalHoursWorkedOverall = employeeKpiSummaries.stream()
                .mapToDouble(EmployeeKpiSummary::getTotalHoursWorked)
                .sum();

        BigDecimal totalConsumptionsOverall = employeeKpiSummaries.stream()
                .map(EmployeeKpiSummary::getTotalConsumptions)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<EmployeeKpiSummary> topEmployeesByHoursWorked = employeeKpiSummaries.stream()
                .filter(s -> s.getTotalHoursWorked() > 0.0)
                .sorted(Comparator.comparingDouble(EmployeeKpiSummary::getTotalHoursWorked).reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<EmployeeKpiSummary> topEmployeesByConsumptions = employeeKpiSummaries.stream()
                .filter(s -> s.getTotalConsumptions().compareTo(BigDecimal.ZERO) > 0)
                .sorted((s1, s2) -> s2.getTotalConsumptions().compareTo(s1.getTotalConsumptions()))
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