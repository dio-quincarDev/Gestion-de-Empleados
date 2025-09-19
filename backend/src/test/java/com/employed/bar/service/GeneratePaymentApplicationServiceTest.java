package com.employed.bar.service;

import com.employed.bar.application.service.GeneratePaymentApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.EmployeeUseCase;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 7);
    }

    @Test
    void testGeneratePayment_SuccessWithAttendance() {
        AttendanceRecordClass record1 = new AttendanceRecordClass();
        record1.setEntryTime(LocalTime.of(9, 0));
        record1.setExitTime(LocalTime.of(17, 0)); // 8 hours

        AttendanceRecordClass record2 = new AttendanceRecordClass();
        record2.setEntryTime(LocalTime.of(9, 0));
        record2.setExitTime(LocalTime.of(13, 0)); // 4 hours

        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(attendanceUseCase.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2));
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), eq(12.0), eq(0.0)))
                .thenReturn(BigDecimal.valueOf(120.0));

        BigDecimal result = generatePaymentApplicationService.generatePayment(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(120.0), result);
        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, times(1)).getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), 12.0, 0.0);
    }

    @Test
    void testGeneratePayment_EmployeeNotFound() {
        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            generatePaymentApplicationService.generatePayment(1L, startDate, endDate);
        });

        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, never()).getAttendanceListByEmployeeAndDateRange(anyLong(), any(), any());
        verify(paymentCalculationUseCase, never()).calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble());
    }

    @Test
    void testGeneratePayment_NoAttendanceRecords() {
        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(attendanceUseCase.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate))
                .thenReturn(Collections.emptyList());
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), eq(0.0), eq(0.0)))
                .thenReturn(BigDecimal.ZERO);

        BigDecimal result = generatePaymentApplicationService.generatePayment(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, times(1)).getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), 0.0, 0.0);
    }

    @Test
    void testGeneratePayment_NullEntryExitTimes() {
        AttendanceRecordClass record1 = new AttendanceRecordClass();
        record1.setEntryTime(null);
        record1.setExitTime(null);

        AttendanceRecordClass record2 = new AttendanceRecordClass();
        record2.setEntryTime(LocalTime.of(9, 0));
        record2.setExitTime(null);

        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(attendanceUseCase.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2));
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), eq(0.0), eq(0.0)))
                .thenReturn(BigDecimal.ZERO);

        BigDecimal result = generatePaymentApplicationService.generatePayment(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, times(1)).getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), 0.0, 0.0);
    }

    @Test
    void testGeneratePayment_PartialEntryExitTimes() {
        AttendanceRecordClass record1 = new AttendanceRecordClass();
        record1.setEntryTime(LocalTime.of(9, 0));
        record1.setExitTime(null);

        AttendanceRecordClass record2 = new AttendanceRecordClass();
        record2.setEntryTime(null);
        record2.setExitTime(LocalTime.of(17, 0));

        when(employeeUseCase.getEmployeeById(1L)).thenReturn(Optional.of(employee));
        when(attendanceUseCase.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate))
                .thenReturn(Arrays.asList(record1, record2));
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), eq(0.0), eq(0.0)))
                .thenReturn(BigDecimal.ZERO);

        BigDecimal result = generatePaymentApplicationService.generatePayment(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(employeeUseCase, times(1)).getEmployeeById(1L);
        verify(attendanceUseCase, times(1)).getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(employee.getHourlyRate(), employee.isPaysOvertime(), employee.getOvertimeRateType(), 0.0, 0.0);
    }
}