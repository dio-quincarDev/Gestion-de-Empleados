package com.employed.bar.service;

import com.employed.bar.application.service.EmployeeApplicationService;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.payment.CashPaymentMethod;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.AttendanceUseCase;
import com.employed.bar.domain.port.in.service.PaymentCalculationUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepositoryPort;

    @Mock
    private AttendanceUseCase attendanceUseCase;

    @Mock
    private PaymentCalculationUseCase paymentCalculationUseCase;

    @InjectMocks
    private EmployeeApplicationService employeeApplicationService;

    private EmployeeClass employee;

    @BeforeEach
    void setUp() {
        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setHourlyRate(BigDecimal.TEN);
        employee.setPaymentMethod(new CashPaymentMethod());
    }

    @Test
    void testCreateEmployee_Success() {
        when(employeeRepositoryPort.findByEmail(employee.getEmail())).thenReturn(Optional.empty());
        when(employeeRepositoryPort.save(any(EmployeeClass.class))).thenReturn(employee);

        EmployeeClass result = employeeApplicationService.createEmployee(employee);

        assertNotNull(result);
        assertEquals(employee, result);
        verify(employeeRepositoryPort, times(1)).findByEmail(employee.getEmail());
        verify(employeeRepositoryPort, times(1)).save(employee);
    }

    @Test
    void testCreateEmployee_EmailAlreadyExists() {
        when(employeeRepositoryPort.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

        assertThrows(EmailAlreadyExistException.class, () -> {
            employeeApplicationService.createEmployee(employee);
        });

        verify(employeeRepositoryPort, times(1)).findByEmail(employee.getEmail());
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }

    @Test
    void testUpdateEmployee_Success() {
        EmployeeClass updatedEmployee = new EmployeeClass();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Doe Updated");
        updatedEmployee.setEmail("john.doe.updated@example.com");
        updatedEmployee.setPaymentMethod(new CashPaymentMethod());

        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepositoryPort.findByEmail(updatedEmployee.getEmail())).thenReturn(Optional.empty());
        when(employeeRepositoryPort.save(any(EmployeeClass.class))).thenReturn(updatedEmployee);

        EmployeeClass result = employeeApplicationService.updateEmployee(1L, updatedEmployee);

        assertNotNull(result);
        assertEquals(updatedEmployee, result);
        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, times(1)).findByEmail(updatedEmployee.getEmail());
        verify(employeeRepositoryPort, times(1)).save(employee);
    }

    @Test
    void testUpdateEmployee_EmployeeNotFound() {
        EmployeeClass updatedEmployee = new EmployeeClass();
        updatedEmployee.setPaymentMethod(new CashPaymentMethod());
        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeApplicationService.updateEmployee(1L, updatedEmployee);
        });

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, never()).findByEmail(anyString());
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }

    @Test
    void testUpdateEmployee_EmailAlreadyExists() {
        EmployeeClass updatedEmployee = new EmployeeClass();
        updatedEmployee.setEmail("existing.email@example.com");
        updatedEmployee.setPaymentMethod(new CashPaymentMethod());

        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepositoryPort.findByEmail(updatedEmployee.getEmail())).thenReturn(Optional.of(new EmployeeClass()));

        assertThrows(EmailAlreadyExistException.class, () -> {
            employeeApplicationService.updateEmployee(1L, updatedEmployee);
        });

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, times(1)).findByEmail(updatedEmployee.getEmail());
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }

    @Test
    void testUpdateHourlyRate_Success() {
        BigDecimal newRate = BigDecimal.valueOf(15.0);
        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepositoryPort.save(any(EmployeeClass.class))).thenReturn(employee);

        EmployeeClass result = employeeApplicationService.updateHourlyRate(1L, newRate);

        assertNotNull(result);
        assertEquals(newRate, result.getHourlyRate());
        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, times(1)).save(employee);
    }

    @Test
    void testUpdateHourlyRate_EmployeeNotFound() {
        BigDecimal newRate = BigDecimal.valueOf(15.0);
        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeApplicationService.updateHourlyRate(1L, newRate);
        });

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }

    @Test
    void testDeleteEmployee_Success() {
        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepositoryPort).delete(employee);

        employeeApplicationService.deleteEmployee(1L);

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, times(1)).delete(employee);
    }

    @Test
    void testDeleteEmployee_NotFound() {
        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        employeeApplicationService.deleteEmployee(1L);

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, never()).delete(any(EmployeeClass.class));
    }

    @Test
    void testCalculateEmployeePay_Success() {
        double regularHours = 40.0;
        double overtimeHours = 10.0;
        BigDecimal expectedPay = BigDecimal.valueOf(500.0);

        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.of(employee));
        when(paymentCalculationUseCase.calculateTotalPay(any(), anyBoolean(), any(), eq(regularHours), eq(overtimeHours)))
                .thenReturn(expectedPay);

        BigDecimal result = employeeApplicationService.calculateEmployeePay(1L, regularHours, overtimeHours);

        assertNotNull(result);
        assertEquals(expectedPay, result);
        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(any(), anyBoolean(), any(), eq(regularHours), eq(overtimeHours));
    }

    @Test
    void testCalculateEmployeePay_EmployeeNotFound() {
        double regularHours = 40.0;
        double overtimeHours = 10.0;

        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeApplicationService.calculateEmployeePay(1L, regularHours, overtimeHours);
        });

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(paymentCalculationUseCase, never()).calculateTotalPay(any(), anyBoolean(), any(), anyDouble(), anyDouble());
    }

    @Test
    void testCreateEmployee_InvalidPaymentMethod() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EmployeeClass(1L, "John Doe", "john.doe@example.com", null, BigDecimal.TEN, null, false, null, null, null, null, null);
        });

        verify(employeeRepositoryPort, never()).findByEmail(anyString());
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }

    @Test
    void testUpdateEmployee_InvalidPaymentMethod() {
        EmployeeClass updatedEmployee = new EmployeeClass();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Doe Updated");
        updatedEmployee.setEmail("john.doe.updated@example.com");
        updatedEmployee.setPaymentMethod(null); // Simulate invalid payment method

        assertThrows(IllegalArgumentException.class, () -> {
            employee.updateWith(updatedEmployee);
        });

        verify(employeeRepositoryPort, never()).findById(anyLong());
        verify(employeeRepositoryPort, never()).findByEmail(anyString());
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }
}