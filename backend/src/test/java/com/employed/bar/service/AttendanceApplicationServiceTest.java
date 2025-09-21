package com.employed.bar.service;

import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
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
import java.util.List;
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

    @Test
    void testCalculateAttendancePercentage_EmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(), any(), any());
    }

    @Test
    void testCalculateAttendancePercentage_NoRecords() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate)).thenReturn(java.util.Collections.emptyList());

        double percentage = attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);

        assertEquals(0.0, percentage, 0.01);
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testCalculateAttendancePercentage_SomeRecords() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);

        AttendanceRecordClass record1 = new AttendanceRecordClass();
        record1.setDate(LocalDate.of(2024, 10, 5));
        AttendanceRecordClass record2 = new AttendanceRecordClass();
        record2.setDate(LocalDate.of(2024, 10, 10));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(java.util.Arrays.asList(record1, record2));

        double percentage = attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);

        // October has 31 days, 2 days with attendance
        assertEquals((2.0 / 31.0) * 100.0, percentage, 0.01);
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testCalculateAttendancePercentage_FullAttendance() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);

        List<AttendanceRecordClass> records = new java.util.ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            AttendanceRecordClass record = new AttendanceRecordClass();
            record.setDate(LocalDate.of(2024, 10, i));
            records.add(record);
        }

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDate, endDate))
                .thenReturn(records);

        double percentage = attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);

        assertEquals(100.0, percentage, 0.01);
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDate, endDate);
    }

    @Test
    void testRegisterAttendance_NullAttendanceRecord() {
        assertThrows(NullPointerException.class, () -> {
            attendanceApplicationService.registerAttendance(null);
        });

        verify(employeeRepository, never()).findById(anyLong());
        verify(attendanceRepositoryPort, never()).save(any(AttendanceRecordClass.class));
    }

    @Test
    void testFindEmployeeAttendances_NullDate() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployee(employee)).thenReturn(java.util.Collections.singletonList(attendanceRecord));

        assertThrows(NullPointerException.class, () -> {
            attendanceApplicationService.findEmployeeAttendances(1L, null);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployee(employee);
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_NullStartDate() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(NullPointerException.class, () -> {
            attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, null, LocalDate.now());
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(), any(), any());
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_NullEndDate() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(NullPointerException.class, () -> {
            attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, LocalDate.now(), null);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(), any(), any());
    }
}