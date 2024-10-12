package com.employed.bar.application;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ReportingApplicationService {
    private final ReportingService reportingService;
    private final EmployeeRepository employeeRepository;
    private final ConsumptionRepository consumptionRepository;

    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        List<Employee> employees = employeeRepository.findAll();
        List<Consumption> consumptions = consumptionRepository.findAll();

        return reportingService.generateCompleteReport( startDate.toLocalDate(), employeeId);
    }
    public void sendWeeklyReports() {
        reportingService.sendWeeklyReports();
    }
}

