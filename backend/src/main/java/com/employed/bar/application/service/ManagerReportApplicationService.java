package com.employed.bar.application.service;

import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.service.ManagerReportServicePort;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.NotificationPort;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.employed.bar.domain.service.ManagerReportCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerReportApplicationService implements ManagerReportServicePort {

    private final EmployeeRepositoryPort employeeRepository;
    private final ReportingUseCase reportingUseCase;
    private final ManagerReportCalculator managerReportCalculator;
    private final NotificationPort notificationPort;
    private final PdfGeneratorPort pdfGeneratorPort;

    public ManagerReportApplicationService(EmployeeRepositoryPort employeeRepository, ReportingUseCase reportingUseCase, ManagerReportCalculator managerReportCalculator, NotificationPort notificationPort, PdfGeneratorPort pdfGeneratorPort) {
        this.employeeRepository = employeeRepository;
        this.reportingUseCase = reportingUseCase;
        this.managerReportCalculator = managerReportCalculator;
        this.notificationPort = notificationPort;
        this.pdfGeneratorPort = pdfGeneratorPort;
    }


    @Override
    public void generateAndSendManagerReport(LocalDate startDate, LocalDate endDate) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();
        List<Report> individualReports = allEmployees.stream()
                .map(employee -> reportingUseCase.generateCompleteReport(startDate, endDate, employee))
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
                .map(employee -> reportingUseCase.generateCompleteReport(startDate, endDate, employee))
                .collect(Collectors.toList());

        ManagerReport managerReport = managerReportCalculator.calculate(allEmployees, individualReports);

        return pdfGeneratorPort.generateManagerReportPdf(managerReport);
    }
}
