package com.employed.bar.service;

import com.employed.bar.application.service.KpiApplicationService;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.kpi.ManagerKpis;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KpiApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepository;

    @Mock
    private AttendanceRepositoryPort attendanceRepository;

    @Mock
    private ConsumptionRepositoryPort consumptionRepositoryPort;

    @InjectMocks
    private KpiApplicationService kpiApplicationService;

    private LocalDate startDate;
    private LocalDate endDate;
    private EmployeeClass activeEmployee1;
    private EmployeeClass activeEmployee2;
    private EmployeeClass inactiveEmployee;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 1, 7);

        activeEmployee1 = new EmployeeClass();
        activeEmployee1.setId(1L);
        activeEmployee1.setName("Employee One");
        activeEmployee1.setStatus(EmployeeStatus.ACTIVE);

        activeEmployee2 = new EmployeeClass();
        activeEmployee2.setId(2L);
        activeEmployee2.setName("Employee Two");
        activeEmployee2.setStatus(EmployeeStatus.ACTIVE);

        inactiveEmployee = new EmployeeClass();
        inactiveEmployee.setId(3L);
        inactiveEmployee.setName("Employee Three");
        inactiveEmployee.setStatus(EmployeeStatus.INACTIVE);
    }

    @Test
    void testGetManagerKpis_SuccessScenario() {
        // Mock employees
        List<EmployeeClass> allEmployees = Arrays.asList(activeEmployee1, activeEmployee2, inactiveEmployee);
        assertEquals(EmployeeStatus.INACTIVE, inactiveEmployee.getStatus(), "Inactive employee status should be INACTIVE"); // DEBUG ASSERTION
        when(employeeRepository.findAll()).thenReturn(allEmployees);

        // Execute the service method
        ManagerKpis result = kpiApplicationService.getManagerKpis(startDate, endDate);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getTotalActiveEmployees());
        assertEquals(1, result.getTotalInactiveEmployees());

        // Verify interactions
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetManagerKpis_NoEmployees() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());

        ManagerKpis result = kpiApplicationService.getManagerKpis(startDate, endDate);

        assertNotNull(result);
        assertEquals(0, result.getTotalActiveEmployees());
        assertEquals(0, result.getTotalInactiveEmployees());
        assertEquals(0.0, result.getTotalHoursWorkedOverall());
        assertEquals(BigDecimal.ZERO, result.getTotalConsumptionsOverall());
        assertTrue(result.getTopEmployeesByHoursWorked().isEmpty());
        assertTrue(result.getTopEmployeesByConsumptions().isEmpty());

        verify(employeeRepository, times(1)).findAll();
        verify(attendanceRepository, never()).findByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDate.class), any(LocalDate.class));
        verify(consumptionRepositoryPort, never()).findByEmployeeAndDateTimeBetween(any(EmployeeClass.class), any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @Test
    void testGetManagerKpis_EmployeesWithNoRecords() {
        List<EmployeeClass> allEmployees = Arrays.asList(activeEmployee1, inactiveEmployee);
        when(employeeRepository.findAll()).thenReturn(allEmployees);

        ManagerKpis result = kpiApplicationService.getManagerKpis(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.getTotalActiveEmployees());
        assertEquals(1, result.getTotalInactiveEmployees());

        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetManagerKpis_AttendanceWithNullTimes() {
        List<EmployeeClass> allEmployees = Collections.singletonList(activeEmployee1);
        when(employeeRepository.findAll()).thenReturn(allEmployees);

        AttendanceRecordClass arNullEntry = new AttendanceRecordClass();
        arNullEntry.setEntryTime(null);
        arNullEntry.setExitTime(LocalTime.of(17, 0));

        AttendanceRecordClass arNullExit = new AttendanceRecordClass();
        arNullExit.setEntryTime(LocalTime.of(9, 0));
        arNullExit.setExitTime(null);

        AttendanceRecordClass arBothNull = new AttendanceRecordClass();
        arBothNull.setEntryTime(null);
        arBothNull.setExitTime(null);

        when(attendanceRepository.findByEmployeeAndDateRange(eq(activeEmployee1), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(arNullEntry, arNullExit, arBothNull));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(activeEmployee1), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.emptyList());

        ManagerKpis result = kpiApplicationService.getManagerKpis(startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.getTotalActiveEmployees());
        assertEquals(0, result.getTotalInactiveEmployees());
        assertEquals(0.0, result.getTotalHoursWorkedOverall());
        assertEquals(BigDecimal.ZERO, result.getTotalConsumptionsOverall());
        assertTrue(result.getTopEmployeesByHoursWorked().isEmpty());
        assertTrue(result.getTopEmployeesByConsumptions().isEmpty());

        verify(employeeRepository, times(1)).findAll();
        verify(attendanceRepository, times(1)).findByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDate.class), any(LocalDate.class));
        verify(consumptionRepositoryPort, times(1)).findByEmployeeAndDateTimeBetween(any(EmployeeClass.class), any(LocalDateTime.class), any(LocalDateTime.class), any());
    }

    @Test
    void testGetManagerKpis_WithActiveAndInactiveEmployees_FullScenario() {
        // Mock empleados: 2 activos + 1 inactivo
        List<EmployeeClass> allEmployees = Arrays.asList(activeEmployee1, activeEmployee2, inactiveEmployee);
        when(employeeRepository.findAll()).thenReturn(allEmployees);

        // 🔹 Asistencia activa
        // Employee 1 → 8 horas (9-17)
        AttendanceRecordClass ar1 = new AttendanceRecordClass();
        ar1.setEntryTime(LocalTime.of(9, 0));
        ar1.setExitTime(LocalTime.of(17, 0));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(activeEmployee1), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(ar1));

        // Employee 2 → 4 horas (9-13)
        AttendanceRecordClass ar2 = new AttendanceRecordClass();
        ar2.setEntryTime(LocalTime.of(9, 0));
        ar2.setExitTime(LocalTime.of(13, 0));
        when(attendanceRepository.findByEmployeeAndDateRange(eq(activeEmployee2), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(ar2));

        // 🔹 Consumos activos
        // Employee 1 → 20.00
        ConsumptionClass cc1 = new ConsumptionClass();
        cc1.setAmount(BigDecimal.valueOf(20.00));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(activeEmployee1), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(cc1));

        // Employee 2 → 10.00
        ConsumptionClass cc2 = new ConsumptionClass();
        cc2.setAmount(BigDecimal.valueOf(10.00));
        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(activeEmployee2), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(cc2));

        // 🔹 Ejecutar servicio
        ManagerKpis result = kpiApplicationService.getManagerKpis(startDate, endDate);

        // ✅ Asserts principales
        assertNotNull(result);
        assertEquals(2, result.getTotalActiveEmployees());
        assertEquals(1, result.getTotalInactiveEmployees());
        assertEquals(12.0, result.getTotalHoursWorkedOverall()); // 8 + 4
        assertEquals(BigDecimal.valueOf(30.00), result.getTotalConsumptionsOverall()); // 20 + 10

        // ✅ Top empleados por horas trabajadas
        assertEquals(2, result.getTopEmployeesByHoursWorked().size());
        assertEquals("Employee One", result.getTopEmployeesByHoursWorked().get(0).getEmployeeName());
        assertEquals(8.0, result.getTopEmployeesByHoursWorked().get(0).getTotalHoursWorked());
        assertEquals("Employee Two", result.getTopEmployeesByHoursWorked().get(1).getEmployeeName());
        assertEquals(4.0, result.getTopEmployeesByHoursWorked().get(1).getTotalHoursWorked());

        // ✅ Top empleados por consumos
        assertEquals(2, result.getTopEmployeesByConsumptions().size());
        assertEquals("Employee One", result.getTopEmployeesByConsumptions().get(0).getEmployeeName());
        assertEquals(BigDecimal.valueOf(20.00), result.getTopEmployeesByConsumptions().get(0).getTotalConsumptions());
        assertEquals("Employee Two", result.getTopEmployeesByConsumptions().get(1).getEmployeeName());
        assertEquals(BigDecimal.valueOf(10.00), result.getTopEmployeesByConsumptions().get(1).getTotalConsumptions());

        // ✅ Verificaciones de interacciones (solo activos)
        verify(employeeRepository, times(1)).findAll();
        verify(attendanceRepository, times(2)).findByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDate.class), any(LocalDate.class));
        verify(consumptionRepositoryPort, times(2)).findByEmployeeAndDateTimeBetween(any(EmployeeClass.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
    }

    @Test
    void testGetManagerKpis_WithMixedAttendanceAndConsumptions_ComplexScenario() {
        // Mock empleados: 3 activos + 2 inactivos
        EmployeeClass activeEmployee3 = new EmployeeClass();
        activeEmployee3.setId(4L);
        activeEmployee3.setName("Employee Four");
        activeEmployee3.setStatus(EmployeeStatus.ACTIVE);

        EmployeeClass inactiveEmployee2 = new EmployeeClass();
        inactiveEmployee2.setId(5L);
        inactiveEmployee2.setName("Employee Five");
        inactiveEmployee2.setStatus(EmployeeStatus.INACTIVE);

        List<EmployeeClass> allEmployees = Arrays.asList(
                activeEmployee1, activeEmployee2, activeEmployee3, inactiveEmployee, inactiveEmployee2
        );
        when(employeeRepository.findAll()).thenReturn(allEmployees);

        // 🔹 Asistencia variada (solo para activos)
        // Employee 1 → 8 horas (9-17) + 4 horas extra (18-22) en otro día
        AttendanceRecordClass ar1_day1 = new AttendanceRecordClass();
        ar1_day1.setEntryTime(LocalTime.of(9, 0));
        ar1_day1.setExitTime(LocalTime.of(17, 0));

        AttendanceRecordClass ar1_day2 = new AttendanceRecordClass();
        ar1_day2.setEntryTime(LocalTime.of(18, 0));
        ar1_day2.setExitTime(LocalTime.of(22, 0));

        when(attendanceRepository.findByEmployeeAndDateRange(eq(activeEmployee1), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(ar1_day1, ar1_day2));

        // Employee 2 → 2 horas (10-12) + 3 horas (13-16) = 5 horas
        AttendanceRecordClass ar2_morning = new AttendanceRecordClass();
        ar2_morning.setEntryTime(LocalTime.of(10, 0));
        ar2_morning.setExitTime(LocalTime.of(12, 0));

        AttendanceRecordClass ar2_afternoon = new AttendanceRecordClass();
        ar2_afternoon.setEntryTime(LocalTime.of(13, 0));
        ar2_afternoon.setExitTime(LocalTime.of(16, 0));

        when(attendanceRepository.findByEmployeeAndDateRange(eq(activeEmployee2), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(ar2_morning, ar2_afternoon));

        // Employee 3 → Solo medio día (9-13) = 4 horas
        AttendanceRecordClass ar3 = new AttendanceRecordClass();
        ar3.setEntryTime(LocalTime.of(9, 0));
        ar3.setExitTime(LocalTime.of(13, 0));

        when(attendanceRepository.findByEmployeeAndDateRange(eq(activeEmployee3), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(ar3));

        // 🔹 Consumos variados (solo para activos)
        // Employee 1 → 25.00 + 15.00 (múltiples consumos)
        ConsumptionClass cc1_1 = new ConsumptionClass();
        cc1_1.setAmount(BigDecimal.valueOf(25.00));
        ConsumptionClass cc1_2 = new ConsumptionClass();
        cc1_2.setAmount(BigDecimal.valueOf(15.00));

        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(activeEmployee1), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Arrays.asList(cc1_1, cc1_2));

        // Employee 2 → Solo 5.00 (consumo bajo)
        ConsumptionClass cc2 = new ConsumptionClass();
        cc2.setAmount(BigDecimal.valueOf(5.00));

        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(activeEmployee2), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(cc2));

        // Employee 3 → 35.00 (consumo alto)
        ConsumptionClass cc3 = new ConsumptionClass();
        cc3.setAmount(BigDecimal.valueOf(35.00));

        when(consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(eq(activeEmployee3), any(LocalDateTime.class), any(LocalDateTime.class), eq(null)))
                .thenReturn(Collections.singletonList(cc3));

        // 🔹 Ejecutar servicio
        ManagerKpis result = kpiApplicationService.getManagerKpis(startDate, endDate);

        // ✅ Asserts principales
        assertNotNull(result);
        assertEquals(3, result.getTotalActiveEmployees());
        assertEquals(2, result.getTotalInactiveEmployees());
        assertEquals(21.0, result.getTotalHoursWorkedOverall(), 0.01);
        assertEquals(BigDecimal.valueOf(80.00), result.getTotalConsumptionsOverall());

        // ✅ Top empleados por horas trabajadas
        assertEquals(3, result.getTopEmployeesByHoursWorked().size());
        assertEquals("Employee One", result.getTopEmployeesByHoursWorked().get(0).getEmployeeName());
        assertEquals(12.0, result.getTopEmployeesByHoursWorked().get(0).getTotalHoursWorked(), 0.01);
        assertEquals("Employee Two", result.getTopEmployeesByHoursWorked().get(1).getEmployeeName());
        assertEquals(5.0, result.getTopEmployeesByHoursWorked().get(1).getTotalHoursWorked(), 0.01);
        assertEquals("Employee Four", result.getTopEmployeesByHoursWorked().get(2).getEmployeeName());
        assertEquals(4.0, result.getTopEmployeesByHoursWorked().get(2).getTotalHoursWorked(), 0.01);

        // ✅ Top empleados por consumos
        assertEquals(3, result.getTopEmployeesByConsumptions().size());
        assertEquals("Employee One", result.getTopEmployeesByConsumptions().get(0).getEmployeeName());
        assertEquals(BigDecimal.valueOf(40.00), result.getTopEmployeesByConsumptions().get(0).getTotalConsumptions());
        assertEquals("Employee Four", result.getTopEmployeesByConsumptions().get(1).getEmployeeName());
        assertEquals(BigDecimal.valueOf(35.00), result.getTopEmployeesByConsumptions().get(1).getTotalConsumptions());
        assertEquals("Employee Two", result.getTopEmployeesByConsumptions().get(2).getEmployeeName());
        assertEquals(BigDecimal.valueOf(5.00), result.getTopEmployeesByConsumptions().get(2).getTotalConsumptions());

        // ✅ Verificaciones de interacciones (solo para empleados activos)
        verify(employeeRepository, times(1)).findAll();
        verify(attendanceRepository, times(3)).findByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDate.class), any(LocalDate.class));
        verify(consumptionRepositoryPort, times(3)).findByEmployeeAndDateTimeBetween(any(EmployeeClass.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(null));
    }

    @Test
    void testGetManagerKpis_NullStartDate() {
        assertThrows(NullPointerException.class, () -> {
            kpiApplicationService.getManagerKpis(null, endDate);
        });

        verify(employeeRepository, never()).findAll();
        verify(attendanceRepository, never()).findByEmployeeAndDateRange(any(), any(), any());
        verify(consumptionRepositoryPort, never()).findByEmployeeAndDateTimeBetween(any(), any(), any(), any());
    }

    @Test
    void testGetManagerKpis_NullEndDate() {
        assertThrows(NullPointerException.class, () -> {
            kpiApplicationService.getManagerKpis(startDate, null);
        });

        verify(employeeRepository, never()).findAll();
        verify(attendanceRepository, never()).findByEmployeeAndDateRange(any(), any(), any());
        verify(consumptionRepositoryPort, never()).findByEmployeeAndDateTimeBetween(any(), any(), any(), any());
    }
}