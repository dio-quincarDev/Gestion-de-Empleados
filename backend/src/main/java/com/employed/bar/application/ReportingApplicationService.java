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

import java.time.LocalDate;
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

    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        List<Employee> employees = employeeRepository.findAll();
        List<Consumption> consumptions = consumptionRepository.findAll();

        return reportingService.generateCompleteReport( startDate.toLocalDate(), employeeId);
    }
    public void sendWeeklyReports() {
        reportingService.sendWeeklyReports();
    }
    public void sendTestEmail(){
        reportingService.sendTestEmail();
    }

    public void sendBulkEmails() {
        // 1. Obtener lista de empleados
        List<Employee> employees = employeeRepository.findAll();

        // 2. Generar reportes para cada empleado
        List<ReportDto> reports = employees.stream()
                .map(employee -> reportingService.generateCompleteReport(LocalDate.now(), employee.getId()))
                .collect(Collectors.toList());

        // 3. Pasar las listas de empleados y reportes al método sendBulkEmails
        reportingService.sendBulkEmails(employees, reports);
    }
}

