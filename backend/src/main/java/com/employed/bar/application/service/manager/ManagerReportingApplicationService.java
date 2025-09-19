package com.employed.bar.application.service.manager;

import com.employed.bar.domain.event.ManagerReportGeneratedEvent;
import com.employed.bar.domain.event.ManagerReportRequestedEvent;
import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.service.ManagerReportCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerReportingApplicationService {

    private final EmployeeRepositoryPort employeeRepository;
    private final ReportingUseCase reportingUseCase;
    private final ManagerReportCalculator managerReportCalculator;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void handleManagerReportRequest(ManagerReportRequestedEvent event) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();
        List<Report> individualReports = allEmployees.stream()
                .map(employee -> reportingUseCase.generateCompleteReport(event.getStartDate(), event.getEndDate(), employee.getId()))
                .collect(Collectors.toList());

        ManagerReport managerReport = managerReportCalculator.calculate(allEmployees, individualReports);

        eventPublisher.publishEvent(new ManagerReportGeneratedEvent(this, managerReport));
    }
}
