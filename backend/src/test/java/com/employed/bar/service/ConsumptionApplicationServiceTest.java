package com.employed.bar.service;

import com.employed.bar.application.service.ConsumptionApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
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
        when(consumptionRepository.sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate)).thenReturn(BigDecimal.TEN);

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result);
        verify(consumptionRepository, times(1)).sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_NoConsumptions() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(consumptionRepository.sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate)).thenReturn(BigDecimal.ZERO);

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(consumptionRepository, times(1)).sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Overloaded_Success() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(consumptionRepository.sumConsumptionByEmployeeAndDateRange(employee, startDateTime, endDateTime)).thenReturn(BigDecimal.TEN);

        BigDecimal result = consumptionApplicationService.calculateTotalConsumptionByEmployee(1L, startDate, endDate);

        assertNotNull(result);
        assertEquals(BigDecimal.TEN, result);
        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, times(1)).sumConsumptionByEmployeeAndDateRange(employee, startDateTime, endDateTime);
    }

    @Test
    void testCalculateTotalConsumptionByEmployee_Overloaded_EmployeeNotFound() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            consumptionApplicationService.calculateTotalConsumptionByEmployee(1L, startDate, endDate);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(consumptionRepository, never()).sumConsumptionByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDateTime.class), any(LocalDateTime.class));
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
        doNothing().when(consumptionRepository).deleteById(1L);

        consumptionApplicationService.deleteConsumption(1L);

        verify(consumptionRepository, times(1)).deleteById(1L);
    }
}