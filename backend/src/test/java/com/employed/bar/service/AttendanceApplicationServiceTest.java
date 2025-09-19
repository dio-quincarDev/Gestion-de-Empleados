package com.employed.bar.service;

import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.AttendanceRecordClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceApplicationServiceTest {

    @Mock
    private EmployeeRepositoryPort employeeRepository;

    @Mock
    private AttendanceRepositoryPort attendanceRepositoryPort;

    @InjectMocks
    private AttendanceApplicationService attendanceApplicationService;

    private EmployeeClass employee;
    private AttendanceRecordClass attendanceRecord;

    @BeforeEach
    void setUp() {
        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("John Doe");

        attendanceRecord = new AttendanceRecordClass();
        attendanceRecord.setEmployee(employee);
        attendanceRecord.setDate(LocalDate.now());
        attendanceRecord.setEntryTime(LocalTime.of(9, 0));
        attendanceRecord.setExitTime(LocalTime.of(17, 0));
    }

    @Test
    void testRegisterAttendance_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.save(any(AttendanceRecordClass.class))).thenReturn(attendanceRecord);

        AttendanceRecordClass result = attendanceApplicationService.registerAttendance(attendanceRecord);

        assertNotNull(result);
        assertEquals(employee, result.getEmployee());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).save(attendanceRecord);
    }

    @Test
    void testRegisterAttendance_EmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            attendanceApplicationService.registerAttendance(attendanceRecord);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).save(any(AttendanceRecordClass.class));
    }

    @Test
    void testRegisterAttendance_NullEmployeeId() {
        attendanceRecord.setEmployee(new EmployeeClass()); // Employee with null id

        assertThrows(IllegalArgumentException.class, () -> {
            attendanceApplicationService.registerAttendance(attendanceRecord);
        });

        verify(employeeRepository, never()).findById(anyLong());
        verify(attendanceRepositoryPort, never()).save(any(AttendanceRecordClass.class));
    }

    @Test
    void testFindEmployeeAttendances_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(java.util.Collections.singletonList(attendanceRecord));

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.findEmployeeAttendances(1L, LocalDate.now());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(attendanceRecord, result.get(0));
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployee(employee);
    }

    @Test
    void testFindEmployeeAttendances_NoRecords() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(java.util.Collections.emptyList());

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.findEmployeeAttendances(1L, LocalDate.now());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployee(employee);
    }

    @Test
    void testFindEmployeeAttendances_EmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            attendanceApplicationService.findEmployeeAttendances(1L, LocalDate.now());
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployee(any(EmployeeClass.class));
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_Success() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate)).thenReturn(java.util.Collections.singletonList(attendanceRecord));

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(attendanceRecord, result.get(0));
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_NoRecords() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate)).thenReturn(java.util.Collections.emptyList());

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_EmployeeNotFound() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_StartDateAfterEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate)).thenReturn(java.util.Collections.emptyList());

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
    }
}