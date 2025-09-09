package com.employed.bar.application.service;

import com.employed.bar.infrastructure.dto.ReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.EmployeeClass;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
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
    private final ReportingUseCase reportingUseCase;
    private final EmployeeRepositoryPort employeeRepository;
    private final ConsumptionRepository consumptionRepository;

    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        List<EmployeeClass> employees = employeeRepository.findAll();
        List<Consumption> consumptions = consumptionRepository.findAll();

        return reportingUseCase.generateCompleteReport( startDate.toLocalDate(), endDate.toLocalDate(), employeeId);
    }
    public void sendWeeklyReports() {
        reportingUseCase.sendWeeklyReports();
    }
    public void sendTestEmail(){
        reportingUseCase.sendTestEmail();
    }

    public void sendBulkEmails() {
        // 1. Obtener lista de empleados
        List<EmployeeClass> employees = employeeRepository.findAll();

        // 2. Generar reportes para cada empleado
        List<ReportDto> reports = employees.stream()
                .map(employee -> reportingUseCase.generateCompleteReport(LocalDate.now(),null, employee.getId()))
                .collect(Collectors.toList());

        // 3. Pasar las listas de empleados y reportes al método sendBulkEmails
        reportingUseCase.sendBulkEmails(employees, reports);
    }
}

