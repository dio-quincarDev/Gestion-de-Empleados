package com.employed.bar.service;

import com.employed.bar.application.service.ReportingApplicationService;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.service.ReportCalculator;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportingApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepository;
    @Mock
    private ConsumptionRepositoryPort consumptionRepositoryPort;
    @Mock
    private AttendanceRepositoryPort attendanceRepositoryPort;
    @Mock
    private ReportCalculator reportCalculator;
    @Mock
    private PaymentCalculationUseCase paymentCalculationUseCase;
    @Mock
    private SendEmployeeReportNotificationUseCase sendEmployeeReportNotificationUseCase;

    @InjectMocks
    private ReportingApplicationService reportingApplicationService;

    private LocalDate startDate;
    private LocalDate endDate;
    private EmployeeClass employee;
    private AttendanceRecordClass attendanceRecord;
    private ConsumptionClass consumptionClass;
    private AttendanceReportLine attendanceReportLine;
    private ConsumptionReportLine consumptionReportLine;
    private HoursCalculation hoursCalculation;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 7);

        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("Test Employee");
        employee.setHourlyRate(BigDecimal.valueOf(10.0));
        employee.setPaysOvertime(true);
        employee.setOvertimeRateType(OvertimeRateType.ONE_HUNDRED_PERCENT);
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setSalary(BigDecimal.ZERO);

        attendanceRecord = new AttendanceRecordClass();
        attendanceRecord.setEntryDateTime(LocalDateTime.of(startDate, LocalTime.of(9, 0)));
        attendanceRecord.setExitDateTime(LocalDateTime.of(startDate, LocalTime.of(17, 0)));

        consumptionClass = new ConsumptionClass();
        consumptionClass.setAmount(BigDecimal.valueOf(50.0));

        attendanceReportLine = new AttendanceReportLine("Test Employee",
                LocalDateTime.of(startDate, LocalTime.of(9, 0)),
                LocalDateTime.of(startDate, LocalTime.of(17, 0)),
                BigDecimal.valueOf(8.0), 100.0);
        consumptionReportLine = new ConsumptionReportLine("Test Employee", LocalDateTime.now(),
                BigDecimal.valueOf(50.0), "Lunch");

        hoursCalculation = new HoursCalculation(BigDecimal.valueOf(8.0), BigDecimal.valueOf(8.0), BigDecimal.ZERO);
    }

    @Test
    void testGenerateCompleteReportForEmployeeById_Success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class),
                eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), any(), any(), anyBoolean(), any(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.valueOf(80.0)); // Example total pay

        Report result = reportingApplicationService.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());

        assertNotNull(result);
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(1, result.getAttendanceLines().size());
        assertEquals(attendanceReportLine, result.getAttendanceLines().get(0));
        assertEquals(1, result.getConsumptionLines().size());
        assertEquals(consumptionReportLine, result.getConsumptionLines().get(0));
        assertEquals(BigDecimal.valueOf(8.0), result.getTotalAttendanceHours());
        assertEquals(BigDecimal.valueOf(50.0), result.getTotalConsumptionAmount());
        assertEquals(BigDecimal.valueOf(80.0), result.getTotalEarnings());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
        verify(reportCalculator, times(1)).mapToAttendanceReportLine(attendanceRecord);
        verify(reportCalculator, times(1)).mapToConsumptionReportLine(consumptionClass);
        verify(reportCalculator, times(1)).calculateHours(Collections.singletonList(attendanceRecord));
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getPaymentType(), employee.getSalary(), employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), hoursCalculation.getRegularHours(), hoursCalculation.getOvertimeHours());
    }

    // ... other tests updated similarly

    @Test
    void testGenerateCompleteReport_SalariedEmployee_PaysOvertime_NoHourlyRate() {
        // GIVEN
        employee.setPaymentType(PaymentType.SALARIED);
        employee.setSalary(new BigDecimal("2000.00"));
        employee.setHourlyRate(BigDecimal.ZERO); // No hourly rate for overtime
        employee.setPaysOvertime(true);

        HoursCalculation overtimeHours = new HoursCalculation(new BigDecimal("45"), new BigDecimal("40"), new BigDecimal("5")); // 5 hours of overtime

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.singletonList(new AttendanceRecordClass()));
        when(reportCalculator.calculateHours(anyList())).thenReturn(overtimeHours);
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), isNull())).thenReturn(Collections.emptyList());

        // Mock the payment calculation
        // Even with overtime hours, the overtime pay should be zero.
        when(paymentCalculationUseCase.calculateTotalPay(
                eq(PaymentType.SALARIED),
                eq(employee.getSalary()),
                eq(employee.getHourlyRate()),
                eq(true),
                any(),
                eq(new BigDecimal("40")),
                eq(new BigDecimal("5"))
        )).thenReturn(employee.getSalary()); // Total pay is just the salary

        // WHEN
        Report result = reportingApplicationService.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());

        // THEN
        assertNotNull(result);
        assertEquals(employee.getSalary().setScale(2), result.getTotalEarnings().setScale(2));
        verify(paymentCalculationUseCase).calculateTotalPay(
                PaymentType.SALARIED,
                employee.getSalary(),
                BigDecimal.ZERO,
                true,
                employee.getOvertimeRateType(),
                new BigDecimal("40"),
                new BigDecimal("5")
        );
    }

    @Test
    void testGenerateCompleteReport_SalariedEmployee_NoAttendance_WithConsumption() {
        // GIVEN
        employee.setPaymentType(PaymentType.SALARIED);
        employee.setSalary(new BigDecimal("2000.00"));

        ConsumptionClass consumption = new ConsumptionClass();
        consumption.setAmount(new BigDecimal("25.00"));
        ConsumptionReportLine consumptionLine = new ConsumptionReportLine(employee.getName(), LocalDateTime.now(), new BigDecimal("25.00"), "Snacks");

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Collections.emptyList());
        when(reportCalculator.calculateHours(anyList())).thenReturn(new HoursCalculation(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), isNull())).thenReturn(Collections.singletonList(consumption));
        when(reportCalculator.mapToConsumptionReportLine(any())).thenReturn(consumptionLine);
        when(paymentCalculationUseCase.calculateTotalPay(any(), any(), any(), anyBoolean(), any(), eq(BigDecimal.ZERO), eq(BigDecimal.ZERO)))
                .thenReturn(BigDecimal.ZERO);

        // WHEN
        Report result = reportingApplicationService.generateCompleteReportForEmployeeById(startDate, endDate, employee.getId());

        // THEN
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getTotalAttendanceHours());
        assertEquals(BigDecimal.ZERO, result.getTotalEarnings());
        assertEquals(new BigDecimal("25.00"), result.getTotalConsumptionAmount());
        assertTrue(result.getAttendanceLines().isEmpty());
        assertFalse(result.getConsumptionLines().isEmpty());
        verify(paymentCalculationUseCase).calculateTotalPay(
                PaymentType.SALARIED,
                employee.getSalary(),
                employee.getHourlyRate(),
                employee.isPaysOvertime(),
                employee.getOvertimeRateType(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    @Test
    void testSendTestEmailToEmployee_Success() {
        // GIVEN
        LocalDate lastActivityDate = LocalDate.of(2025, 10, 28);
        LocalDateTime lastActivityDateTime = lastActivityDate.atStartOfDay();
        LocalDate expectedStartDate = lastActivityDate.minusDays(6);
        LocalDate expectedEndDate = lastActivityDate;

        AttendanceRecordClass latestAttendance = new AttendanceRecordClass();
        latestAttendance.setEntryDateTime(lastActivityDateTime);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findTopByEmployeeOrderByEntryDateTimeDesc(employee)).thenReturn(Optional.of(latestAttendance));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(eq(employee), eq(expectedStartDate.atStartOfDay()), eq(expectedEndDate.atTime(23, 59, 59))))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), eq(expectedStartDate.atStartOfDay()), eq(expectedEndDate.atTime(23, 59, 59)), eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), any(), any(), anyBoolean(), any(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.valueOf(80.0));

        // WHEN
        reportingApplicationService.sendTestEmailToEmployee(employee.getId());

        // THEN
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(attendanceRepositoryPort, times(1)).findTopByEmployeeOrderByEntryDateTimeDesc(employee);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(eq(employee), eq(expectedStartDate.atStartOfDay()), eq(expectedEndDate.atTime(23, 59, 59)));
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(eq(employee), eq(expectedStartDate.atStartOfDay()), eq(expectedEndDate.atTime(23, 59, 59)), eq(null));
        verify(reportCalculator, times(1)).mapToAttendanceReportLine(attendanceRecord);
        verify(reportCalculator, times(1)).mapToConsumptionReportLine(consumptionClass);
        verify(reportCalculator, times(1)).calculateHours(Collections.singletonList(attendanceRecord));
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(any(), any(), any(), anyBoolean(), any(), any(BigDecimal.class), any(BigDecimal.class));
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(anyList(), anyList());
    }}