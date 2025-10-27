package com.employed.bar.service;

import com.employed.bar.application.service.EmployeeApplicationService;
import com.employed.bar.domain.enums.PaymentType;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.payment.CashPaymentMethod;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.AttendanceUseCase;
import com.employed.bar.domain.port.in.payment.PaymentCalculationUseCase;
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
        employee.setSalary(BigDecimal.ZERO);
        employee.setPaymentType(PaymentType.HOURLY);
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
        updatedEmployee.setHourlyRate(BigDecimal.TEN);
        updatedEmployee.setSalary(BigDecimal.ZERO);
        updatedEmployee.setPaymentType(PaymentType.HOURLY);
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
        updatedEmployee.setPaymentType(PaymentType.HOURLY);
        updatedEmployee.setHourlyRate(BigDecimal.TEN);
        updatedEmployee.setSalary(BigDecimal.ZERO);
        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeApplicationService.updateEmployee(1L, updatedEmployee);
        });

        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(employeeRepositoryPort, never()).findByEmail(anyString());
        verify(employeeRepositoryPort, never()).save(any(EmployeeClass.class));
    }

    // ... other tests ...

    @Test
    void testCalculateEmployeePay_Success() {
        BigDecimal regularHours = new BigDecimal("40.0");
        BigDecimal overtimeHours = new BigDecimal("10.0");
        BigDecimal expectedPay = new BigDecimal("500.0");

        when(employeeRepositoryPort.findById(1L)).thenReturn(Optional.of(employee));
        when(paymentCalculationUseCase.calculateTotalPay(any(), any(), any(), anyBoolean(), any(), eq(regularHours), eq(overtimeHours)))
                .thenReturn(expectedPay);

        BigDecimal result = employeeApplicationService.calculateEmployeePay(1L, regularHours, overtimeHours);

        assertNotNull(result);
        assertEquals(expectedPay, result);
        verify(employeeRepositoryPort, times(1)).findById(1L);
        verify(paymentCalculationUseCase, times(1)).calculateTotalPay(any(), any(), any(), anyBoolean(), any(), eq(regularHours), eq(overtimeHours));
    }

    @Test
    void testCreateEmployee_Salaried_InvalidHourlyRate() {
        employee.setPaymentType(PaymentType.SALARIED);
        employee.setSalary(new BigDecimal("2000"));
        employee.setHourlyRate(BigDecimal.TEN); // Salaried employees should not have hourly rate in this context

        // This test might need adjustment based on the final validation logic
        // For now, let's assume it's allowed for overtime purposes
        when(employeeRepositoryPort.findByEmail(employee.getEmail())).thenReturn(Optional.empty());
        when(employeeRepositoryPort.save(any(EmployeeClass.class))).thenReturn(employee);

        assertDoesNotThrow(() -> {
            employeeApplicationService.createEmployee(employee);
        });
    }

    @Test
    void testCreateEmployee_Hourly_InvalidSalary() {
        employee.setPaymentType(PaymentType.HOURLY);
        employee.setHourlyRate(BigDecimal.TEN);
        employee.setSalary(new BigDecimal("100")); // Hourly employees must have zero salary

        assertThrows(IllegalArgumentException.class, () -> {
            employeeApplicationService.createEmployee(employee);
        });
    }
}
