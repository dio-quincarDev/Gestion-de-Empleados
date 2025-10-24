package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.service.ReportCalculator;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportingApplicationService implements ReportingUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final ConsumptionRepositoryPort consumptionRepositoryPort;
    private final AttendanceRepositoryPort attendanceRepositoryPort;
    private final ReportCalculator reportCalculator;
    private final PaymentCalculationUseCase paymentCalculationUseCase;
    private final SendEmployeeReportNotificationUseCase sendEmployeeReportNotificationUseCase;

    public ReportingApplicationService(EmployeeRepositoryPort employeeRepository, ConsumptionRepositoryPort consumptionRepositoryPort, AttendanceRepositoryPort attendanceRepositoryPort, ReportCalculator reportCalculator, PaymentCalculationUseCase paymentCalculationUseCase, SendEmployeeReportNotificationUseCase sendEmployeeReportNotificationUseCase) {
        this.employeeRepository = employeeRepository;
        this.consumptionRepositoryPort = consumptionRepositoryPort;
        this.attendanceRepositoryPort = attendanceRepositoryPort;
        this.reportCalculator = reportCalculator;
        this.paymentCalculationUseCase = paymentCalculationUseCase;
        this.sendEmployeeReportNotificationUseCase = sendEmployeeReportNotificationUseCase;
    }


    @Override
    public Report generateCompleteReportForEmployeeById(LocalDate startDate, LocalDate endDate, Long employeeId) {
        if (startDate == null || endDate == null || employeeId == null) {
            throw new IllegalArgumentException("Start date, end date, and employee ID must not be null");
        }
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return generateCompleteReportForEmployee(startDate, endDate, employee);
    }

    private Report generateCompleteReportForEmployee(LocalDate startDate, LocalDate endDate, EmployeeClass employee) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AttendanceRecordClass> attendanceRecords = attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
        List<AttendanceReportLine> attendanceLines = attendanceRecords.stream()
                .map(reportCalculator::mapToAttendanceReportLine)
                .collect(Collectors.toList());

        List<ConsumptionReportLine> consumptionLines = consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(employee, startDateTime,
                        endDateTime, null).stream()
                .map(reportCalculator::mapToConsumptionReportLine)
                .collect(Collectors.toList());

        HoursCalculation hoursCalculation = reportCalculator.calculateHours(attendanceRecords);
        BigDecimal totalConsumption = consumptionLines.stream()
                .map(ConsumptionReportLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEarnings = paymentCalculationUseCase.calculateTotalPay(
                employee.getPaymentType(),
                employee.getSalary(),
                employee.getHourlyRate(),
                employee.isPaysOvertime(),
                employee.getOvertimeRateType(),
                hoursCalculation.getRegularHours(),
                hoursCalculation.getOvertimeHours()
        );

        return new Report(employee.getId(), attendanceLines, consumptionLines, hoursCalculation, totalConsumption, totalEarnings);
    }


    @Override
    public void sendTestEmailToEmployee(Long employeeId) {
        System.out.println("🔍 [SERVICE] Buscando employee con ID: " + employeeId);
        try {
            EmployeeClass employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
            System.out.println("✅ [SERVICE] Employee encontrado: " + employee.getName());

            // Find the last activity date for the employee
            LocalDate endDate = attendanceRepositoryPort.findTopByEmployeeOrderByEntryDateTimeDesc(employee)
                    .map(att -> att.getEntryDateTime().toLocalDate())
                    .orElse(LocalDate.now()); // Fallback to today if no activity found

            LocalDate startDate = endDate.minusDays(6);

            System.out.println("📊 [SERVICE] Generando reporte para el rango de fechas: " + startDate + " a " + endDate);

            Report report = generateCompleteReportForEmployee(startDate, endDate, employee);
            System.out.println("📈 [SERVICE] Reporte generado:");
            System.out.println("   - AttendanceLines: " + report.getAttendanceLines().size());
            System.out.println("   - ConsumptionLines: " + report.getConsumptionLines().size());
            System.out.println("   - TotalHours: " + report.getTotalAttendanceHours());
            System.out.println("   - TotalConsumption: " + report.getTotalConsumptionAmount());

            System.out.println("📧 [SERVICE] Enviando a notificación...");
            sendEmployeeReportNotificationUseCase.sendReport(Collections.singletonList(employee), Collections.singletonList(report));
            System.out.println("🚀 [SERVICE] Notificación enviada exitosamente");
        } catch (Exception e) {
            System.out.println("❌ [SERVICE] ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void generateAndSendWeeklyReport(LocalDate startDate, LocalDate endDate) {
        List<EmployeeClass> allEmployees = employeeRepository.findAll();
        List<Report> reports = allEmployees.stream()
                .map(employee -> generateCompleteReportForEmployee(startDate, endDate, employee))
                .collect(Collectors.toList());

        sendEmployeeReportNotificationUseCase.sendReport(allEmployees, reports);
    }
}

