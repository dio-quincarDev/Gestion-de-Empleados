package com.employed.bar.application.service;

import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ReportingPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportingApplicationService implements ReportingUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final ConsumptionRepository consumptionRepository;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private final ReportingPort reportingPort; // Assuming this will be for sending emails, etc.

    @Override
    public Report generateCompleteReport(LocalDate startDate, LocalDate endDate, Long employeeId) {
        List<EmployeeClass> employeesToReport = employeeId == null
                ? employeeRepository.findAll()
                : Collections.singletonList(employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AttendanceReportLine> attendanceLines = employeesToReport.stream()
                .flatMap(employee -> attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate).stream())
                .map(this::mapToAttendanceReportLine)
                .collect(Collectors.toList());

        List<ConsumptionReportLine> consumptionLines = employeesToReport.stream()
                .flatMap(employee -> consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDateTime, endDateTime, null).stream())
                .map(this::mapToConsumptionReportLine)
                .collect(Collectors.toList());

        double totalHours = attendanceLines.stream().mapToDouble(AttendanceReportLine::getWorkedHours).sum();
        BigDecimal totalConsumption = consumptionLines.stream()
                .map(ConsumptionReportLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Report(attendanceLines, consumptionLines, totalHours, totalConsumption);
    }

    private AttendanceReportLine mapToAttendanceReportLine(AttendanceRecordClass record) {
        // This mapping logic might need adjustment based on how workedHours is calculated
        double workedHours = 0; // Placeholder for actual calculation
        if (record.getEntryTime() != null && record.getExitTime() != null) {
            workedHours = java.time.Duration.between(record.getEntryTime(), record.getExitTime()).toMinutes() / 60.0;
        }
        return new AttendanceReportLine(
                record.getEmployee().getName(),
                record.getDate(),
                record.getEntryTime(),
                record.getExitTime(),
                workedHours,
                100.0 // Placeholder for percentage
        );
    }

    private ConsumptionReportLine mapToConsumptionReportLine(ConsumptionClass consumption) {
        return new ConsumptionReportLine(
                consumption.getEmployee().getName(),
                consumption.getConsumptionDate(),
                consumption.getAmount(),
                consumption.getDescription()
        );
    }

    @Override
    public void sendWeeklyReports() {
        // Logic to define date range for weekly reports
        // Then call generateCompleteReport and send via reportingPort
    }

    @Override
    public void sendTestEmail() {
        // Send a test email via reportingPort
    }

    @Override
    public void sendBulkEmails(List<EmployeeClass> employees, List<Report> reports) {
        // Use reportingPort to send bulk emails
        reportingPort.sendBulkEmails(employees, reports);
    }

    @Override
    public void sendTestBulkEmails() {
        // Logic to generate test data and send via reportingPort
    }

    @Override
    public void sendTestEmailToEmployee(Long employeeId) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Define a fixed date range for the test report
        LocalDate testDate = LocalDate.parse("2024-10-10");

        // Generate the domain report
        Report report = this.generateCompleteReport(testDate, testDate, employeeId);

        // Delegate the sending to the outbound port
        reportingPort.sendReportByEmail(employee, report);
    }
}

