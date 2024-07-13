package com.employed.bar.application;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.out.ReportingPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportingApplicationService {
    private final ReportingService reportingService;
    private final EmployeeRepository employeeRepository;
    private final ConsumptionRepository consumptionRepository;

    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Employee> employees = employeeRepository.findAll();
        List<Consumption> consumptions = consumptionRepository.findAll();

        List<AttendanceReportDto> attendanceReports = employees.stream()
                .map(e -> reportingService.generateEmployeeReport(e, startDate, endDate))
                .collect(Collectors.toList());
        List<ConsumptionReportDto> consumptionReports = List.of(reportingService.generateConsumptionReport(consumptions, startDate, endDate));

        return new ReportDto(attendanceReports, consumptionReports);
    }
}

