package com.employed.bar.service;

import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.enums.AttendanceStatus;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidAttendanceDataException;
import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;
import com.employed.bar.domain.port.out.AttendanceRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Mock
    private ScheduleRepositoryPort scheduleRepositoryPort;

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
        attendanceRecord.setEntryDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)));
        attendanceRecord.setExitDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)));
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
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime)).thenReturn(java.util.Collections.singletonList(attendanceRecord));

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(attendanceRecord, result.get(0));
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_NoRecords() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime)).thenReturn(java.util.Collections.emptyList());

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
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
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(EmployeeClass.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_StartDateAfterEndDate() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime)).thenReturn(java.util.Collections.emptyList());

        java.util.List<AttendanceRecordClass> result = attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, startDate, endDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
    }

    @Test
    void testCalculateAttendancePercentage_EmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> {
            attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testCalculateAttendancePercentage_NoRecords() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime)).thenReturn(java.util.Collections.emptyList());

        double percentage = attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);

        assertEquals(0.0, percentage, 0.01);
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
    }

    @Test
    void testCalculateAttendancePercentage_SomeRecords() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        AttendanceRecordClass record1 = new AttendanceRecordClass();
        record1.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 10, 5), LocalTime.MIDNIGHT));
        AttendanceRecordClass record2 = new AttendanceRecordClass();
        record2.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 10, 10), LocalTime.MIDNIGHT));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime))
                .thenReturn(java.util.Arrays.asList(record1, record2));

        double percentage = attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);

        // October has 31 days, 2 days with attendance
        assertEquals((2.0 / 31.0) * 100.0, percentage, 0.01);
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
    }

    @Test
    void testCalculateAttendancePercentage_FullAttendance() {
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AttendanceRecordClass> records = new java.util.ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            AttendanceRecordClass record = new AttendanceRecordClass();
            record.setEntryDateTime(LocalDateTime.of(LocalDate.of(2024, 10, i), LocalTime.MIDNIGHT));
            records.add(record);
        }

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(attendanceRepositoryPort.findByEmployeeAndDateRange(employee, startDateTime, endDateTime))
                .thenReturn(records);

        double percentage = attendanceApplicationService.calculateAttendancePercentage(1L, 2024, 10, 1);

        assertEquals(100.0, percentage, 0.01);
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).findByEmployeeAndDateRange(employee, startDateTime, endDateTime);
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
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetAttendanceListByEmployeeAndDateRange_NullEndDate() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(NullPointerException.class, () -> {
            attendanceApplicationService.getAttendanceListByEmployeeAndDateRange(1L, LocalDate.now(), null);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).findByEmployeeAndDateRange(any(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testRegisterAttendance_ExitTimeBeforeEntryTime() {
        attendanceRecord.setEntryDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)));
        attendanceRecord.setExitDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0)));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(InvalidAttendanceDataException.class, () -> {
            attendanceApplicationService.registerAttendance(attendanceRecord);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, never()).save(any(AttendanceRecordClass.class));
    }

    @Test
    void testRegisterAttendance_StatusPresent() {
        LocalDateTime entryTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime exitTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        ScheduleClass schedule = new ScheduleClass();
        schedule.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0))); // Schedule starts at 9:00

        attendanceRecord.setEntryDateTime(entryTime);
        attendanceRecord.setExitDateTime(exitTime);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.findByEmployeeAndDate(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(java.util.Collections.singletonList(schedule));
        when(attendanceRepositoryPort.save(any(AttendanceRecordClass.class))).thenAnswer(invocation -> {
            AttendanceRecordClass record = invocation.getArgument(0);
            assertNotNull(record.getStatus());
            assertEquals(AttendanceStatus.PRESENT, record.getStatus());
            return record;
        });

        AttendanceRecordClass result = attendanceApplicationService.registerAttendance(attendanceRecord);

        assertNotNull(result);
        assertEquals(AttendanceStatus.PRESENT, result.getStatus());
        verify(employeeRepository, times(1)).findById(1L);
        verify(attendanceRepositoryPort, times(1)).save(attendanceRecord);
    }

    @Test
    void testRegisterAttendance_StatusLate() {
        LocalDateTime entryTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 30)); // Arrives late
        LocalDateTime exitTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        ScheduleClass schedule = new ScheduleClass();
        schedule.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0))); // Schedule starts at 9:00

        attendanceRecord.setEntryDateTime(entryTime);
        attendanceRecord.setExitDateTime(exitTime);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.findByEmployeeAndDate(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(java.util.Collections.singletonList(schedule));
        when(attendanceRepositoryPort.save(any(AttendanceRecordClass.class))).thenAnswer(invocation -> {
            AttendanceRecordClass record = invocation.getArgument(0);
            assertNotNull(record.getStatus());
            assertEquals(AttendanceStatus.LATE, record.getStatus());
            return record;
        });

        AttendanceRecordClass result = attendanceApplicationService.registerAttendance(attendanceRecord);

        assertNotNull(result);
        assertEquals(AttendanceStatus.LATE, result.getStatus());
        verify(employeeRepository, times(1)).findById(1L);
        verify(scheduleRepositoryPort, times(1)).findByEmployeeAndDate(eq(employee), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(attendanceRepositoryPort, times(1)).save(attendanceRecord);
    }
}