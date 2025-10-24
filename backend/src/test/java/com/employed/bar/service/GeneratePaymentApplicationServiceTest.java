package com.employed.bar.service;

import com.employed.bar.application.service.GeneratePaymentApplicationService;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.report.HoursCalculation;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.AttendanceUseCase;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
import com.employed.bar.domain.service.ReportCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneratePaymentApplicationServiceTest {

    @Mock
    private AttendanceUseCase attendanceUseCase;

    @Mock
    private EmployeeUseCase employeeUseCase;

    @Mock
    private PaymentCalculationUseCase paymentCalculationUseCase;

    @Mock
    private ReportCalculator reportCalculator;

    @InjectMocks
    private GeneratePaymentApplicationService generatePaymentApplicationService;

    private EmployeeClass employee;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setHourlyRate(BigDecimal.valueOf(10.0));
        employee.setPaysOvertime(true);
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setSalary(BigDecimal.ZERO);

        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 7);
    }

    @Test
    void testGeneratePayment_SuccessWithAttendance() {
        AttendanceRecordClass record1 = new AttendanceRecordClass();
        record1.setEntryDateTime(LocalDateTime.of(startDate, LocalTime.of(9, 0)));
        record1.setExitDateTime(LocalDateTime.of(startDate, LocalTime.of(17, 0))); // 8 hours

        AttendanceRecordClass record2 = new AttendanceRecordClass();
        record2.setEntryDateTime(LocalDateTime.of(startDate, LocalTime.of(9, 0)));
        record2.setExitDateTime(LocalDateTime.of(startDate, LocalTime.of(13, 0))); // 4 hours

        HoursCalculation hoursCalculation = new HoursCalculation(new BigDecimal("12.0"), new BigDecimal("12.0"), BigDecimal.ZERO);

        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(attendanceUseCase.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2));
        when(reportCalculator.calculateHours(anyList())).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), any(), any(), anyBoolean(), any(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(BigDecimal.valueOf(120.0));

        BigDecimal result = generatePaymentApplicationService.generatePayment(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(120.0), result);
        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, times(1)).getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getPaymentType(), employee.getSalary(), employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), new BigDecimal("12.0"), BigDecimal.ZERO);
    }

    @Test
    void testGeneratePayment_EmployeeNotFound() {
        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            generatePaymentApplicationService.generatePayment(1L, startDate, endDate);
        });

        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, never()).getAttendanceListByEmployeeAndDateRange(anyLong(), any(), any());
        verify(paymentCalculationUseCase, never()).calculateTotalPay(any(), any(), any(), anyBoolean(), any(), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    void testGeneratePayment_NoAttendanceRecords() {
        HoursCalculation hoursCalculation = new HoursCalculation(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(attendanceUseCase.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(reportCalculator.calculateHours(anyList())).thenReturn(hoursCalculation);
        when(paymentCalculationUseCase.calculateTotalPay(any(), any(), any(), anyBoolean(), any(), eq(BigDecimal.ZERO), eq(BigDecimal.ZERO)))
                .thenReturn(BigDecimal.ZERO);

        BigDecimal result = generatePaymentApplicationService.generatePayment(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, times(1)).getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getPaymentType(), employee.getSalary(), employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), BigDecimal.ZERO, BigDecimal.ZERO);
    }
}