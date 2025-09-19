package com.employed.bar.application.service.manager;

import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.GenerateManagerReportPdfUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.PdfGeneratorPort;
import com.employed.bar.domain.service.ManagerReportCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenerateManagerReportPdfApplicationService implements GenerateManagerReportPdfUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final ReportingUseCase reportingUseCase;
    private final ManagerReportCalculator managerReportCalculator;
    private final PdfGeneratorPort pdfGeneratorPort;

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
