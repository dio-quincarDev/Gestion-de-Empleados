package com.employed.bar.application.service;

import com.employed.bar.domain.event.ManagerReportGeneratedEvent;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.ManagerReportServicePort;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.NotificationPort;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.employed.bar.domain.service.ManagerReportCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerReportApplicationService implements ManagerReportServicePort {

    private final EmployeeRepositoryPort employeeRepository;
    private final ReportingUseCase reportingUseCase;
    private final ManagerReportCalculator managerReportCalculator;
    private final NotificationPort notificationPort;
    private final PdfGeneratorPort pdfGeneratorPort;

    @Override
    public void generateAndSendManagerReport(LocalDate startDate, LocalDate endDate) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();
        List<Report> individualReports = allEmployees.stream()
                .map(employee -> reportingUseCase.generateCompleteReport(startDate, endDate, employee.getId()))
                .collect(Collectors.toList());

        ManagerReport managerReport = managerReportCalculator.calculate(allEmployees, individualReports);

        // TODO: Make the manager's email configurable
        String managerEmail = "manager@example.com";
        notificationPort.sendManagerReportByEmail(managerEmail, managerReport);

    }

    @Override
    public byte[] generateManagerReportPdf(LocalDate startDate, LocalDate endDate) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();
        List<Report> individualReports = allEmployees.stream()
                .map(employee -> reportingUseCase.generateCompleteReport(startDate, endDate, employee.getId()))
                .collect(Collectors.toList());

        ManagerReport managerReport = managerReportCalculator.calculate(allEmployees, individualReports);

        return pdfGeneratorPort.generateManagerReportPdf(managerReport);
    }
}
