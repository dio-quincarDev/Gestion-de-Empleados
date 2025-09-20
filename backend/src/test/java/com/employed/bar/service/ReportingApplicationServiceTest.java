package com.employed.bar.service;

import com.employed.bar.application.service.ReportingApplicationService;
import com.employed.bar.domain.enums.OvertimeRateType;
import com.employed.bar.domain.event.TestEmailRequestedEvent;
import com.employed.bar.domain.event.WeeklyReportRequestedEvent;
import com.employed.bar.domain.model.report.AttendanceReportLine;
import com.employed.bar.domain.model.report.ConsumptionReportLine;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.report.ReportCalculator;
import com.employed.bar.domain.model.report.hours.HoursCalculation;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    private ApplicationEventPublisher eventPublisher;
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

        attendanceRecord = new AttendanceRecordClass();
        attendanceRecord.setEntryTime(LocalTime.of(9, 0));
        attendanceRecord.setExitTime(LocalTime.of(17, 0));

        consumptionClass = new ConsumptionClass();
        consumptionClass.setAmount(BigDecimal.valueOf(50.0));

        attendanceReportLine = new AttendanceReportLine("Test Employee", LocalDate.now(),
                LocalTime.of(9,0), LocalTime.of(17,0), 8.0, 100.0);
        consumptionReportLine = new ConsumptionReportLine("Test Employee", LocalDateTime.now(),
                BigDecimal.valueOf(50.0), "Lunch");

        hoursCalculation = new HoursCalculation(8.0, 8.0, 0.0);
    }

    @Test
    void testGenerateCompleteReport_Success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class),
                eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(80.0)); // Example total pay

        Report result = reportingApplicationService.generateCompleteReport(startDate, endDate, employee.getId());

        assertNotNull(result);
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(1, result.getAttendanceLines().size());
        assertEquals(attendanceReportLine, result.getAttendanceLines().get(0));
        assertEquals(1, result.getConsumptionLines().size());
        assertEquals(consumptionReportLine, result.getConsumptionLines().get(0));
        assertEquals(8.0, result.getTotalAttendanceHours());
        assertEquals(BigDecimal.valueOf(50.0), result.getTotalConsumptionAmount());
        assertEquals(BigDecimal.valueOf(80.0), result.getTotalEarnings());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
        verify(reportCalculator, times(1)).mapToAttendanceReportLine(attendanceRecord);
        verify(reportCalculator, times(1)).mapToConsumptionReportLine(consumptionClass);
        verify(reportCalculator, times(1)).calculateHours(Collections.singletonList(attendanceRecord));
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), hoursCalculation.getRegularHours(), hoursCalculation.getOvertimeHours());
    }

    @Test
    void testGenerateCompleteReport_EmployeeNotFound() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            reportingApplicationService.generateCompleteReport(startDate, endDate, employee.getId());
        });

        assertEquals("Employee not found", thrown.getMessage());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verifyNoInteractions(attendanceRepositoryPort, consumptionRepositoryPort, reportCalculator, paymentCalculationUseCase);
    }

    @Test
    void testGenerateCompleteReport_NoAttendanceRecords() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.emptyList())).thenReturn(new HoursCalculation(0.0, 0.0, 0.0));
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(0.0));

        Report result = reportingApplicationService.generateCompleteReport(startDate, endDate, employee.getId());

        assertNotNull(result);
        assertEquals(employee.getId(), result.getEmployeeId());
        assertTrue(result.getAttendanceLines().isEmpty());
        assertEquals(1, result.getConsumptionLines().size());
        assertTrue(Double.compare(0.0, result.getTotalAttendanceHours()) == 0);
        assertEquals(BigDecimal.valueOf(50.0), result.getTotalConsumptionAmount());
        assertEquals(BigDecimal.valueOf(0.0), result.getTotalEarnings());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
        verify(reportCalculator, never()).mapToAttendanceReportLine(any());
        verify(reportCalculator, times(1)).mapToConsumptionReportLine(consumptionClass);
        verify(reportCalculator, times(1)).calculateHours(Collections.emptyList());
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), 0.0, 0.0);
    }

    @Test
    void testGenerateCompleteReport_NoConsumptionRecords() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.emptyList());
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(80.0));

        Report result = reportingApplicationService.generateCompleteReport(startDate, endDate, employee.getId());

        assertNotNull(result);
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(1, result.getAttendanceLines().size());
        assertEquals(attendanceReportLine, result.getAttendanceLines().get(0));
        assertTrue(result.getConsumptionLines().isEmpty());
        assertEquals(8.0, result.getTotalAttendanceHours());
        assertEquals(BigDecimal.ZERO, result.getTotalConsumptionAmount());
        assertEquals(BigDecimal.valueOf(80.0), result.getTotalEarnings());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
        verify(reportCalculator, times(1)).mapToAttendanceReportLine(attendanceRecord);
        verify(reportCalculator, never()).mapToConsumptionReportLine(any());
        verify(reportCalculator, times(1)).calculateHours(Collections.singletonList(attendanceRecord));
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), hoursCalculation.getRegularHours(), hoursCalculation.getOvertimeHours());
    }

    @Test
    void testGenerateCompleteReport_AttendanceWithNullTimes() {
        AttendanceRecordClass arNullEntry = new AttendanceRecordClass();
        arNullEntry.setEntryTime(null);
        arNullEntry.setExitTime(LocalTime.of(17, 0));

        AttendanceRecordClass arNullExit = new AttendanceRecordClass();
        arNullExit.setEntryTime(LocalTime.of(9, 0));
        arNullExit.setExitTime(null);

        AttendanceRecordClass arBothNull = new AttendanceRecordClass();
        arBothNull.setEntryTime(null);
        arBothNull.setExitTime(null);

        List<AttendanceRecordClass> attendanceRecordsWithNulls = Arrays.asList(arNullEntry, arNullExit, arBothNull);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(attendanceRecordsWithNulls);
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.emptyList());
        when(reportCalculator.mapToAttendanceReportLine(any(AttendanceRecordClass.class)))
                .thenReturn(new AttendanceReportLine("Test Employee", LocalDate.now(), null, null, 0.0, 0.0)); // Mocked line for null times
        when(reportCalculator.calculateHours(attendanceRecordsWithNulls)).thenReturn(new HoursCalculation(0.0, 0.0, 0.0));
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(0.0));

        Report result = reportingApplicationService.generateCompleteReport(startDate, endDate, employee.getId());

        assertNotNull(result);
        assertEquals(employee.getId(), result.getEmployeeId());
        assertEquals(3, result.getAttendanceLines().size());
        assertTrue(result.getConsumptionLines().isEmpty());
        assertTrue(Double.compare(0.0, result.getTotalAttendanceHours()) == 0);
        assertEquals(BigDecimal.ZERO, result.getTotalConsumptionAmount());
        assertEquals(BigDecimal.valueOf(0.0), result.getTotalEarnings());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
        verify(reportCalculator, times(3)).mapToAttendanceReportLine(any(AttendanceRecordClass.class));
        verify(reportCalculator, never()).mapToConsumptionReportLine(any());
        verify(reportCalculator, times(1)).calculateHours(attendanceRecordsWithNulls);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), 0.0, 0.0);
    }

    @Test
    void testHandleWeeklyReportRequest_Success() {
        WeeklyReportRequestedEvent event = new WeeklyReportRequestedEvent(this, startDate, endDate);
        List<EmployeeClass> employees = Collections.singletonList(employee);

        when(employeeRepository.findAll()).thenReturn(employees);
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(),
                hoursCalculation.getRegularHours(), hoursCalculation.getOvertimeHours()))
                .thenReturn(BigDecimal.valueOf(80.0));

        reportingApplicationService.handleWeeklyReportRequest(event);

        ArgumentCaptor<List<Report>> reportsCaptor = ArgumentCaptor.forClass(List.class);
        verify(employeeRepository, times(1)).findAll();
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(eq(employees), reportsCaptor.capture());

        List<Report> capturedReports = reportsCaptor.getValue();
        assertEquals(1, capturedReports.size());
        Report capturedReport = capturedReports.get(0);

        assertNotNull(capturedReport);
        assertEquals(employee.getId(), capturedReport.getEmployeeId());
        assertEquals(1, capturedReport.getAttendanceLines().size());
        assertEquals(attendanceReportLine, capturedReport.getAttendanceLines().get(0));
        assertEquals(1, capturedReport.getConsumptionLines().size());
        assertEquals(consumptionReportLine, capturedReport.getConsumptionLines().get(0));
        assertEquals(8.0, capturedReport.getTotalAttendanceHours());
        assertEquals(BigDecimal.valueOf(50.0), capturedReport.getTotalConsumptionAmount());
        assertEquals(BigDecimal.valueOf(80.0), capturedReport.getTotalEarnings());
    }

    @Test
    void testHandleWeeklyReportRequest_NoEmployees() {
        WeeklyReportRequestedEvent event = new WeeklyReportRequestedEvent(this, startDate, endDate);

        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        reportingApplicationService.handleWeeklyReportRequest(event);

        verify(employeeRepository, times(1)).findAll();
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(eq(Collections.emptyList()), eq(Collections.emptyList()));
        verifyNoMoreInteractions(sendEmployeeReportNotificationUseCase);
    }

    @Test
    void testSendTestEmailToEmployee_Success() {
        LocalDate testDate = LocalDate.parse("2024-10-10");
        LocalDateTime startDateTime = testDate.atStartOfDay();
        LocalDateTime endDateTime = testDate.atTime(23, 59, 59);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, testDate, testDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(employee, startDateTime, endDateTime, null))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(),
                hoursCalculation.getRegularHours(), hoursCalculation.getOvertimeHours()))
                .thenReturn(BigDecimal.valueOf(80.0));

        reportingApplicationService.sendTestEmailToEmployee(employee.getId());

        ArgumentCaptor<List<Report>> reportsCaptor = ArgumentCaptor.forClass(List.class);
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(eq(Collections.singletonList(employee)), reportsCaptor.capture());

        List<Report> capturedReports = reportsCaptor.getValue();
        assertEquals(1, capturedReports.size());
        Report capturedReport = capturedReports.get(0);

        assertNotNull(capturedReport);
        assertEquals(employee.getId(), capturedReport.getEmployeeId());
    }

    @Test
    void testSendTestEmailToEmployee_EmployeeNotFound() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            reportingApplicationService.sendTestEmailToEmployee(employee.getId());
        });

        assertEquals("Employee not found", thrown.getMessage());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verifyNoInteractions(sendEmployeeReportNotificationUseCase);
    }

    @Test
    void testHandleTestEmailRequest_Success() {
        TestEmailRequestedEvent event = new TestEmailRequestedEvent(this, employee.getId());
        LocalDate testDate = LocalDate.parse("2024-10-10");
        LocalDateTime startDateTime = testDate.atStartOfDay();
        LocalDateTime endDateTime = testDate.atTime(23, 59, 59);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, testDate, testDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(employee, startDateTime, endDateTime, null))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(),
                hoursCalculation.getRegularHours(), hoursCalculation.getOvertimeHours()))
                .thenReturn(BigDecimal.valueOf(80.0));

        reportingApplicationService.handleTestEmailRequest(event);

        ArgumentCaptor<List<Report>> reportsCaptor = ArgumentCaptor.forClass(List.class);
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(eq(Collections.singletonList(employee)), reportsCaptor.capture());

        List<Report> capturedReports = reportsCaptor.getValue();
        assertEquals(1, capturedReports.size());
        Report capturedReport = capturedReports.get(0);

        assertNotNull(capturedReport);
        assertEquals(employee.getId(), capturedReport.getEmployeeId());
    }
}