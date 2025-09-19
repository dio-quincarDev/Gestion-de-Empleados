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

    @Spy
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
    private List<EmployeeClass> allEmployees;

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

        attendanceReportLine = new AttendanceReportLine("Test Employee", LocalDate.now(), LocalTime.of(9,0), LocalTime.of(17,0), 8.0, 100.0);
        consumptionReportLine = new ConsumptionReportLine("Test Employee", LocalDateTime.now(), BigDecimal.valueOf(50.0), "Lunch");

        hoursCalculation = new HoursCalculation(8.0, 8.0, 0.0);
        allEmployees = Arrays.asList(employee);
    }

    @Test
    void testGenerateCompleteReport_Success() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
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
        verifyNoInteractions(attendanceRepositoryPort);
        verifyNoInteractions(consumptionRepositoryPort);
        verifyNoInteractions(reportCalculator);
        verifyNoInteractions(paymentCalculationUseCase);
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

        EmployeeClass streamEmployee = new EmployeeClass();
        streamEmployee.setId(employee.getId()); // Use the same ID as the test employee
        streamEmployee.setName(employee.getName()); // Use the same name

        when(employeeRepository.findAll()).thenReturn(Collections.singletonList(streamEmployee));
        when(employeeRepository.findById(streamEmployee.getId())).thenReturn(Optional.of(streamEmployee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(streamEmployee, startDate, endDate))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(streamEmployee), eq(startDate.atStartOfDay()), eq(endDate.atTime(23, 59, 59)), eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(80.0)); // Example total pay

        reportingApplicationService.handleWeeklyReportRequest(event);

        ArgumentCaptor<List> reportsCaptor = ArgumentCaptor.forClass(List.class);

        verify(employeeRepository, times(1)).findAll();
        verify(reportingApplicationService, times(1)).generateCompleteReport(startDate, endDate, employee.getId());
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(eq(Collections.singletonList(streamEmployee)), reportsCaptor.capture());

        List<Report> capturedReports = reportsCaptor.getValue();
        assertEquals(1, capturedReports.size());
        Report capturedReport = capturedReports.get(0);

        // Assert properties of the captured report
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

        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(any(), any());
    }

    @Test
    void testSendTestEmailToEmployee_Success() {
        Report report = new Report(employee.getId(), Collections.emptyList(), Collections.emptyList(), 0.0, BigDecimal.ZERO, BigDecimal.ZERO);

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(reportingApplicationService.generateCompleteReport(any(LocalDate.class), any(LocalDate.class), eq(employee.getId()))).thenReturn(report);

        reportingApplicationService.sendTestEmailToEmployee(employee.getId());

        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(reportingApplicationService, times(1)).generateCompleteReport(any(LocalDate.class), any(LocalDate.class), eq(employee.getId()));
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(Collections.singletonList(employee), Collections.singletonList(report));
    }

    @Test
    void testSendTestEmailToEmployee_EmployeeNotFound() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            reportingApplicationService.sendTestEmailToEmployee(employee.getId());
        });

        assertEquals("Employee not found", thrown.getMessage());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verifyNoInteractions(reportingApplicationService);
        verifyNoInteractions(sendEmployeeReportNotificationUseCase);
    }

    @Test
    void testHandleTestEmailRequest_Success() {
        TestEmailRequestedEvent event = new TestEmailRequestedEvent(this, employee.getId());

        // Mock internal dependencies of sendTestEmailToEmployee
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, LocalDate.parse("2024-10-10"), LocalDate.parse("2024-10-10")))
                .thenReturn(Collections.singletonList(attendanceRecord));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(consumptionClass));
        when(reportCalculator.mapToAttendanceReportLine(attendanceRecord)).thenReturn(attendanceReportLine);
        when(reportCalculator.mapToConsumptionReportLine(consumptionClass)).thenReturn(consumptionReportLine);
        when(reportCalculator.calculateHours(Collections.singletonList(attendanceRecord))).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble()))
                .thenReturn(BigDecimal.valueOf(80.0)); // Example total pay

        reportingApplicationService.handleTestEmailRequest(event);

        verify(reportingApplicationService, times(1)).sendTestEmailToEmployee(employee.getId());
        verify(employeeRepository, times(1)).findById(employee.getId());
        verify(reportingApplicationService, times(1)).generateCompleteReport(LocalDate.parse("2024-10-10"), LocalDate.parse("2024-10-10"), employee.getId());
        verify(sendEmployeeReportNotificationUseCase, times(1)).sendReport(Collections.singletonList(employee), Collections.singletonList(any(Report.class)));
    }
}