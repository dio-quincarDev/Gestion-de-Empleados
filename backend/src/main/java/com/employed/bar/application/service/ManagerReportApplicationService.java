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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerReportApplicationService implements ManagerReportServicePort {

    private final EmployeeRepositoryPort employeeRepository;
    private final ReportingUseCase reportingUseCase;
    private final ManagerReportCalculator managerReportCalculator;
    private final NotificationPort notificationPort;
    private final PdfGeneratorPort pdfGeneratorPort;

    public ManagerReportApplicationService(EmployeeRepositoryPort employeeRepository,
                                           ReportingUseCase reportingUseCase, ManagerReportCalculator managerReportCalculator,
                                           NotificationPort notificationPort, PdfGeneratorPort pdfGeneratorPort) {
        this.employeeRepository = employeeRepository;
        this.reportingUseCase = reportingUseCase;
        this.managerReportCalculator = managerReportCalculator;
        this.notificationPort = notificationPort;
        this.pdfGeneratorPort = pdfGeneratorPort;
    }


    @Override
    public void generateAndSendManagerReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        List<EmployeeClass> allEmployees = employeeRepository.findAll(Pageable.unpaged()).getContent();
        List<Report> individualReports = allEmployees.stream()
                .map(employee -> reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId()))
                .collect(Collectors.toList());

        ManagerReport managerReport = managerReportCalculator.calculate(allEmployees, individualReports);

        // Generate the PDF report as a byte array
        byte[] pdfBytes = pdfGeneratorPort.generateManagerReportPdf(managerReport, startDate, endDate);

        // TODO: Make the manager's email configurable
        String managerEmail = "manager@example.com";
        notificationPort.sendManagerReportByEmail(managerEmail, managerReport, pdfBytes, startDate, endDate);
    }

    @Override
    public byte[] generateManagerReportPdf(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        List<EmployeeClass> allEmployees = employeeRepository.findAll(Pageable.unpaged()).getContent();
        List<Report> individualReports = allEmployees.stream()
                .map(employee -> reportingUseCase.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId()))
                .collect(Collectors.toList());

        ManagerReport managerReport = managerReportCalculator.calculate(allEmployees, individualReports);

        return pdfGeneratorPort.generateManagerReportPdf(managerReport, startDate, endDate);
    }
}