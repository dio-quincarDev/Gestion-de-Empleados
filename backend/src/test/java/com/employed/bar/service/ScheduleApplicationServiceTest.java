package com.employed.bar.service;

import com.employed.bar.application.service.ScheduleApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.ScheduleNotFoundException;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.model.strucuture.ScheduleClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleApplicationServiceTest {

    @Mock
    private ScheduleRepositoryPort scheduleRepositoryPort;

    @Mock
    private EmployeeRepositoryPort employeeRepository;

    @InjectMocks
    private ScheduleApplicationService scheduleApplicationService;

    private EmployeeClass employee;
    private ScheduleClass schedule;

    @BeforeEach
    void setUp() {
        employee = new EmployeeClass();
        employee.setId(1L);
        employee.setName("Test Employee");

        schedule = new ScheduleClass();
        schedule.setId(10L);
        schedule.setEmployee(employee);
        schedule.setStartTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        schedule.setEndTime(LocalDateTime.of(2023, 1, 1, 17, 0));
    }

    @Test
    void createSchedule_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.save(any(ScheduleClass.class))).thenReturn(schedule);

        ScheduleClass result = scheduleApplicationService.createSchedule(schedule);

        assertNotNull(result);
        assertEquals(employee, result.getEmployee());
        verify(employeeRepository, times(1)).findById(1L);
        verify(scheduleRepositoryPort, times(1)).save(schedule);
    }

    @Test
    void createSchedule_ThrowsException_WhenEmployeeIdIsNull() {
        schedule.setEmployee(new EmployeeClass()); // Employee with null ID

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            scheduleApplicationService.createSchedule(schedule);
        });

        assertEquals("Employee ID must be set on the schedule.", exception.getMessage());
        verifyNoInteractions(employeeRepository, scheduleRepositoryPort);
    }

    @Test
    void createSchedule_ThrowsException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            scheduleApplicationService.createSchedule(schedule);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1L);
        verifyNoInteractions(scheduleRepositoryPort);
    }

    @Test
    void getScheduleById_Found() {
        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.of(schedule));

        ScheduleClass result = scheduleApplicationService.getScheduleById(10L);

        assertNotNull(result);
        assertEquals(schedule, result);
        verify(scheduleRepositoryPort, times(1)).findById(10L);
    }

    @Test
    void getScheduleById_NotFound_ReturnsNull() {
        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.empty());

        ScheduleClass result = scheduleApplicationService.getScheduleById(10L);

        assertNull(result);
        verify(scheduleRepositoryPort, times(1)).findById(10L);
    }

    @Test
    void getSchedulesByEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.findByEmployee(employee)).thenReturn(Collections.singletonList(schedule));

        List<ScheduleClass> result = scheduleApplicationService.getSchedulesByEmployee(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(schedule, result.get(0));
        verify(employeeRepository, times(1)).findById(1L);
        verify(scheduleRepositoryPort, times(1)).findByEmployee(employee);
    }

    @Test
    void getSchedulesByEmployee_EmployeeNotFound_ReturnsEmptyList() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        List<ScheduleClass> result = scheduleApplicationService.getSchedulesByEmployee(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(employeeRepository, times(1)).findById(1L);
        verifyNoInteractions(scheduleRepositoryPort);
    }

    @Test
    void deleteSchedule_Success() {
        doNothing().when(scheduleRepositoryPort).deleteById(10L);

        scheduleApplicationService.deleteSchedule(10L);

        verify(scheduleRepositoryPort, times(1)).deleteById(10L);
    }

    @Test
    void updateSchedule_Success() {
        ScheduleClass updatedDetails = new ScheduleClass();
        updatedDetails.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        updatedDetails.setEndTime(LocalDateTime.of(2023, 1, 1, 18, 0));

        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.of(schedule));
        when(scheduleRepositoryPort.save(any(ScheduleClass.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleClass result = scheduleApplicationService.updateSchedule(10L, updatedDetails);

        assertNotNull(result);
        assertEquals(updatedDetails.getStartTime(), result.getStartTime());
        assertEquals(updatedDetails.getEndTime(), result.getEndTime());
        verify(scheduleRepositoryPort, times(1)).findById(10L);
        verify(scheduleRepositoryPort, times(1)).save(schedule);
    }

    @Test
    void updateSchedule_ThrowsException_WhenScheduleNotFound() {
        ScheduleClass updatedDetails = new ScheduleClass();
        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.empty());

        ScheduleNotFoundException exception = assertThrows(ScheduleNotFoundException.class, () -> {
            scheduleApplicationService.updateSchedule(10L, updatedDetails);
        });

        assertEquals("Schedule not found with ID: 10", exception.getMessage());
        verify(scheduleRepositoryPort, times(1)).findById(10L);
        verify(scheduleRepositoryPort, never()).save(any(ScheduleClass.class));
    }
}