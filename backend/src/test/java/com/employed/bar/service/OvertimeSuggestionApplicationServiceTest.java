package com.employed.bar.service;

import com.employed.bar.application.service.OvertimeSuggestionApplicationService;

import com.employed.bar.domain.model.payment.OvertimeSuggestion;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.model.payment.PaymentMethod;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OvertimeSuggestionApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepositoryPort;

    @Mock
    private AttendanceRepositoryPort attendanceRepositoryPort;

    @InjectMocks
    private OvertimeSuggestionApplicationService overtimeSuggestionService;

    @Test
    void testGenerateSuggestions_WithOvertime() {
        // Given
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        AttendanceRecordClass record = new AttendanceRecordClass();
        record.setEmployee(employee);
        record.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 20), LocalTime.of(9, 0)));
        record.setExitDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 20), LocalTime.of(18, 0))); // 9 hours

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(Collections.singletonList(record));

        // When
        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        // Then
        assertFalse(suggestions.isEmpty());
        assertEquals(1, suggestions.size());
        OvertimeSuggestion suggestion = suggestions.get(0);
        assertEquals(employee, suggestion.getEmployee());
        assertEquals(LocalDate.of(2024, 5, 20), suggestion.getDate());
        assertEquals(60, suggestion.getExtraMinutesWorked()); // 9 hours = 540 minutes, 540 - 480 = 60
    }

    @Test
    void testGenerateSuggestions_NoOvertime() {
        // Given
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("Jane Doe");
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        AttendanceRecordClass record = new AttendanceRecordClass();
        record.setEmployee(employee);
        record.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 21), LocalTime.of(9, 0)));
        record.setExitDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 21), LocalTime.of(17, 0))); // 8 hours

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(Collections.singletonList(record));

        // When
        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        // Then
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_EmployeeWithOvertimePay() {
        // Given
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("Peter Jones");
        employee.setPaysOvertime(true);
        employee.setPaymentMethod(paymentMethod);

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));

        // When
        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        // Then
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_NoAttendanceRecords() {
        // Given
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("Noel Gallagher");
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(Collections.emptyList());

        // When
        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        // Then
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_MultipleEmployees() {
        // Given
        PaymentMethod paymentMethod = mock(PaymentMethod.class);

        EmployeeClass employeeWithOvertime = new EmployeeClass();
        employeeWithOvertime.setId(1L);
        employeeWithOvertime.setName("Liam Gallagher");
        employeeWithOvertime.setPaysOvertime(false);
        employeeWithOvertime.setPaymentMethod(paymentMethod);

        EmployeeClass employeePaysOvertime = new EmployeeClass();
        employeePaysOvertime.setId(2L);
        employeePaysOvertime.setName("Damon Albarn");
        employeePaysOvertime.setPaysOvertime(true);
        employeePaysOvertime.setPaymentMethod(paymentMethod);

        EmployeeClass employeeNoOvertime = new EmployeeClass();
        employeeNoOvertime.setId(3L);
        employeeNoOvertime.setName("Graham Coxon");
        employeeNoOvertime.setPaysOvertime(false);
        employeeNoOvertime.setPaymentMethod(paymentMethod);

        AttendanceRecordClass overtimeRecord = new AttendanceRecordClass();
        overtimeRecord.setEmployee(employeeWithOvertime);
        overtimeRecord.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 23), LocalTime.of(9, 0)));
        overtimeRecord.setExitDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 23), LocalTime.of(18, 0))); // 9 hours

        AttendanceRecordClass noOvertimeRecord = new AttendanceRecordClass();
        noOvertimeRecord.setEmployee(employeeNoOvertime);
        noOvertimeRecord.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 23), LocalTime.of(9, 0)));
        noOvertimeRecord.setExitDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 23), LocalTime.of(17, 0))); // 8 hours

        when(employeeRepositoryPort.findAll()).thenReturn(List.of(employeeWithOvertime, employeePaysOvertime, employeeNoOvertime));
        when(attendanceRepositoryPort.findByEmployee(employeeWithOvertime)).thenReturn(Collections.singletonList(overtimeRecord));
        when(attendanceRepositoryPort.findByEmployee(employeeNoOvertime)).thenReturn(Collections.singletonList(noOvertimeRecord));

        // When
        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        // Then
        assertEquals(1, suggestions.size());
        OvertimeSuggestion suggestion = suggestions.get(0);
        assertEquals(employeeWithOvertime, suggestion.getEmployee());
        assertEquals(60, suggestion.getExtraMinutesWorked());
    }

    @Test
    void testGenerateSuggestions_NullEmployeeRepositoryFindAll() {
        when(employeeRepositoryPort.findAll()).thenReturn(null);

        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_NullAttendanceRepositoryFindByEmployee() {
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(null);

        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_AttendanceRecordWithNullDate() {
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        AttendanceRecordClass record = new AttendanceRecordClass();
        record.setEmployee(employee);
        record.setEntryDateTime(null); // Null date, so entryDateTime is null
        record.setExitDateTime(null); // Null date, so exitDateTime is null

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(Collections.singletonList(record));

        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_ExactlyEightHoursWorked() {
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        AttendanceRecordClass record = new AttendanceRecordClass();
        record.setEmployee(employee);
        record.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 20), LocalTime.of(9, 0)));
        record.setExitDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 20), LocalTime.of(17, 0))); // 8 hours

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(Collections.singletonList(record));

        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testGenerateSuggestions_LessThanEightHoursWorked() {
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        EmployeeClass employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setPaysOvertime(false);
        employee.setPaymentMethod(paymentMethod);

        AttendanceRecordClass record = new AttendanceRecordClass();
        record.setEmployee(employee);
        record.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 20), LocalTime.of(9, 0)));
        record.setExitDateTime(LocalDateTime.of(LocalDate.of(2024, 5, 20), LocalTime.of(16, 0))); // 7 hours

        when(employeeRepositoryPort.findAll()).thenReturn(Collections.singletonList(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(Collections.singletonList(record));

        List<OvertimeSuggestion> suggestions = overtimeSuggestionService.generateSuggestions();

        assertTrue(suggestions.isEmpty());
    }
}
