package com.employed.bar.service;

import com.employed.bar.application.service.ConsumptionApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsumptionApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepository;

    @Mock
    private ConsumptionRepositoryPort consumptionRepository;

    @InjectMocks
    private ConsumptionApplicationService consumptionApplicationService;

    private EmployeeClass employee;
    private ConsumptionClass consumption;

    @BeforeEach
    void setUp() {
        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("John Doe");

        consumption = new ConsumptionClass();
        consumption.setEmployee(employee);
        consumption.setAmount(BigDecimal.TEN);
        consumption.setDescription("Coffee");
        consumption.setConsumptionDate(LocalDateTime.now());
    }

    @Test
    void testCreateConsumption_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(consumptionRepository.save(any(ConsumptionClass.class))).thenReturn(consumption);

        ConsumptionClass result = consumptionApplicationService.createConsumption(consumption);

        assertNotNull(result);
        assertEquals(employee, result.getEmployee());
        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, times(1)).save(consumption);
    }

    @Test
    void testCreateConsumption_EmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            consumptionApplicationService.createConsumption(consumption);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, never()).save(any(ConsumptionClass.class));
    }

    @Test
    void testCreateConsumption_DataIntegrityViolation() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(consumptionRepository.save(any(ConsumptionClass.class))).thenThrow(new org.springframework.dao.DataIntegrityViolationException("Null amount"));

        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            consumptionApplicationService.createConsumption(consumption);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, times(1)).save(consumption);
    }

    @Test
    void testGetConsumptionById_Success() {
        when(consumptionRepository.findById(1L)).thenReturn(Optional.of(consumption));

        Optional<ConsumptionClass> result = consumptionApplicationService.getConsumptionById(1L);

        assertTrue(result.isPresent());
        assertEquals(consumption, result.get());
        verify(consumptionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetConsumptionById_NotFound() {
        when(consumptionRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<ConsumptionClass> result = consumptionApplicationService.getConsumptionById(1L);

        assertFalse(result.isPresent());
        verify(consumptionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetConsumptionByEmployee_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, null)).thenReturn(java.util.Collections.singletonList(consumption));

        java.util.List<ConsumptionClass> result = consumptionApplicationService.getConsumptionByEmployee(employee, startDate, endDate, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(consumption, result.get(0));
        verify(consumptionRepository, times(1)).findByEmployeeAndDateTimeBetween(employee, startDate, endDate, null);
    }

    @Test
    void testGetConsumptionByEmployee_NoRecords() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, null)).thenReturn(java.util.Collections.emptyList());

        java.util.List<ConsumptionClass> result = consumptionApplicationService.getConsumptionByEmployee(employee, startDate, endDate, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(consumptionRepository, times(1)).findByEmployeeAndDateTimeBetween(employee, startDate, endDate, null);
    }

    @Test
    void testGetConsumptionByEmployee_WithDescription() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        String description = "Coffee";
        when(consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, description)).thenReturn(java.util.Collections.singletonList(consumption));

        java.util.List<ConsumptionClass> result = consumptionApplicationService.getConsumptionByEmployee(employee, startDate, endDate, description);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(consumption, result.get(0));
        verify(consumptionRepository, times(1)).findByEmployeeAndDateTimeBetween(employee, startDate, endDate, description);
    }

    @Test
    void testGetConsumptionByEmployee_StartDateAfterEndDate() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().minusDays(7);
        when(consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, null)).thenReturn(java.util.Collections.emptyList());

        java.util.List<ConsumptionClass> result = consumptionApplicationService.getConsumptionByEmployee(employee, startDate, endDate, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(consumptionRepository, times(1)).findByEmployeeAndDateTimeBetween(employee, startDate, endDate, null);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(consumptionRepository.sumConsumptionByEmployeeAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(BigDecimal.TEN);

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);

        assertNotNull(result);
        verify(consumptionRepository, times(1)).sumConsumptionByEmployeeAndDateRange(employee.getId(), startDate, endDate);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_NoConsumptions() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(consumptionRepository.sumConsumptionByEmployeeAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(BigDecimal.ZERO);

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(consumptionRepository, times(1)).sumConsumptionByEmployeeAndDateRange(employee.getId(), startDate, endDate);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Overloaded_Success() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(consumptionRepository.sumConsumptionByEmployeeAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(BigDecimal.TEN);

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionByEmployee(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result);
        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, times(1)).sumConsumptionByEmployeeAndDateRange(employee.getId(), startDateTime, endDateTime);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Overloaded_EmployeeNotFound() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            consumptionApplicationService.calculateTotalConsumptionByEmployee(1L, startDate, endDate);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, never()).sumConsumptionByEmployeeAndDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testCalculateTotalConsumptionForAllEmployees_Success() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(consumptionRepository.sumTotalConsumptionByDateRange(startDate, endDate)).thenReturn(BigDecimal.valueOf(100));

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionForAllEmployees(startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(100), result);
        verify(consumptionRepository, times(1)).sumTotalConsumptionByDateRange(startDate, endDate);
    }

    @Test
    void testDeleteConsumption_Success() {
        when(consumptionRepository.findById(1L)).thenReturn(Optional.of(consumption));
        doNothing().when(consumptionRepository).deleteById(1L);

        consumptionApplicationService.deleteConsumption(1L);

        verify(consumptionRepository, times(1)).findById(1L);
        verify(consumptionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCreateConsumption_NullConsumptionClass() {
        assertThrows(NullPointerException.class, () -> {
            consumptionApplicationService.createConsumption(null);
        });

        verify(employeeRepository, never()).findById(anyLong());
        verify(consumptionRepository, never()).save(any(ConsumptionClass.class));
    }

    @Test
    void testCreateConsumption_NullEmployeeInConsumption() {
        consumption.setEmployee(null);

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.createConsumption(consumption);
        });

        verify(employeeRepository, never()).findById(any());
        verify(consumptionRepository, never()).save(any(ConsumptionClass.class));
    }

    @Test
    void testCreateConsumption_NullEmployeeIdInConsumption() {
        employee.setId(null);
        consumption.setEmployee(employee);

        assertThrows(EmployeeNotFoundException.class, () -> {
            consumptionApplicationService.createConsumption(consumption);
        });

        verify(employeeRepository, times(1)).findById(null);
        verify(consumptionRepository, never()).save(any(ConsumptionClass.class));
    }

    @Test
    void testGetConsumptionByEmployee_NullEmployee() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.getConsumptionByEmployee(null, startDate, endDate, null);
        });

        verify(consumptionRepository, never()).findByEmployeeAndDateTimeBetween(any(), any(), any(), any());
    }

    @Test
    void testGetConsumptionByEmployee_NullStartDate() {
        LocalDateTime endDate = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.getConsumptionByEmployee(employee, null, endDate, null);
        });

        verify(consumptionRepository, never()).findByEmployeeAndDateTimeBetween(any(), any(), any(), any());
    }

    @Test
    void testGetConsumptionByEmployee_NullEndDate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.getConsumptionByEmployee(employee, startDate, null, null);
        });

        verify(consumptionRepository, never()).findByEmployeeAndDateTimeBetween(any(), any(), any(), any());
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Overloaded_NullStartDate() {
        LocalDate endDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(NullPointerException.class, () -> {
            consumptionApplicationService.calculateTotalConsumptionByEmployee(1L, null, endDate);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, never()).sumConsumptionByEmployeeAndDateRange(any(), any(), any());
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Overloaded_NullEndDate() {
        LocalDate startDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(NullPointerException.class, () -> {
            consumptionApplicationService.calculateTotalConsumptionByEmployee(1L, startDate, null);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, never()).sumConsumptionByEmployeeAndDateRange(any(), any(), any());
    }

    @Test
    void testCalculateTotalConsumptionForAllEmployees_NullStartDate() {
        LocalDateTime endDate = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.calculateTotalConsumptionForAllEmployees(null, endDate);
        });

        verify(consumptionRepository, never()).sumTotalConsumptionByDateRange(any(), any());
    }

    @Test
    void testUpdateConsumption_NullEmployee() {
        consumption.setId(1L);
        consumption.setEmployee(null);

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.updateConsumption(consumption);
        });

        verify(consumptionRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).findById(anyLong());
        verify(consumptionRepository, never()).save(any(ConsumptionClass.class));
    }

    @Test
    void testCalculateTotalConsumptionForAllEmployees_NullEndDate() {
        LocalDateTime startDate = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.calculateTotalConsumptionForAllEmployees(startDate, null);
        });

        verify(consumptionRepository, never()).sumTotalConsumptionByDateRange(any(), any());
    }
}