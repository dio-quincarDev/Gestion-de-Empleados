package com.employed.bar.application.service;

import com.employed.bar.domain.event.TestEmailRequestedEvent;
import com.employed.bar.domain.event.WeeklyReportRequestedEvent;
import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.report.ReportCalculator;
import com.employed.bar.domain.model.report.hours.HoursCalculation;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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
    private final ApplicationEventPublisher eventPublisher;
    private final ReportCalculator reportCalculator;
    private final PaymentCalculationUseCase paymentCalculationUseCase;
    private final SendEmployeeReportNotificationUseCase sendEmployeeReportNotificationUseCase;


    @EventListener
    public void handleWeeklyReportRequest(WeeklyReportRequestedEvent event) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();
        List<Report> reports = allEmployees.stream()
                .map(employee -> generateCompleteReport(event.getStartDate(), event.getEndDate(), employee.getId()))
                .collect(Collectors.toList());

        sendEmployeeReportNotificationUseCase.sendReport(allEmployees, reports);
    }

    @Override
    public Report generateCompleteReport(LocalDate startDate, LocalDate endDate, Long employeeId) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AttendanceRecordClass> attendanceRecords = attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate);
        List<AttendanceReportLine> attendanceLines = attendanceRecords.stream()
                .map(reportCalculator::mapToAttendanceReportLine)
                .collect(Collectors.toList());

        List<ConsumptionReportLine> consumptionLines = consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDateTime, endDateTime, null).stream()
                .map(reportCalculator::mapToConsumptionReportLine)
                .collect(Collectors.toList());

        HoursCalculation hoursCalculation = reportCalculator.calculateHours(attendanceRecords);
        BigDecimal totalConsumption = consumptionLines.stream()
                .map(ConsumptionReportLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEarnings = paymentCalculationUseCase.calculateTotalPay(
                employee.getHourlyRate(),
                employee.isPaysOvertime(),
                employee.getOvertimeRateType(),
                hoursCalculation.getRegularHours(),
                hoursCalculation.getOvertimeHours()
        );

        return new Report(employeeId, attendanceLines, consumptionLines, hoursCalculation.getTotalHours(), totalConsumption, totalEarnings);
    }

    @EventListener
    public void handleTestEmailRequest(TestEmailRequestedEvent event) {
        sendTestEmailToEmployee(event.getEmployeeId());
    }

    @Override
    public void sendTestEmailToEmployee(Long employeeId) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate testDate = LocalDate.parse("2024-10-10");

        Report report = this.generateCompleteReport(testDate, testDate, employeeId);

        sendEmployeeReportNotificationUseCase.sendReport(Collections.singletonList(employee), Collections.singletonList(report));
    }
}

