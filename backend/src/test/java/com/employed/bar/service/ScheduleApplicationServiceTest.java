package com.employed.bar.service;

import com.employed.bar.application.service.ScheduleApplicationService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.ScheduleNotFoundException;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;
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
    void getScheduleById_NotFound_ThrowsException() {
        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.empty());

        ScheduleNotFoundException exception = assertThrows(ScheduleNotFoundException.class, () -> {
            scheduleApplicationService.getScheduleById(10L);
        });

        assertEquals("Schedule not found with ID: 10", exception.getMessage());
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
    void getSchedulesByEmployee_EmployeeNotFound_ThrowsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            scheduleApplicationService.getSchedulesByEmployee(1L);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(1L);
        verifyNoInteractions(scheduleRepositoryPort);
    }

    @Test
    void deleteSchedule_Success() {
        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.of(schedule));
        doNothing().when(scheduleRepositoryPort).deleteById(10L);

        scheduleApplicationService.deleteSchedule(10L);

        verify(scheduleRepositoryPort, times(1)).findById(10L);
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

    @Test
    void testCreateSchedule_NullSchedule() {
        assertThrows(NullPointerException.class, () -> {
            scheduleApplicationService.createSchedule(null);
        });

        verifyNoInteractions(employeeRepository, scheduleRepositoryPort);
    }

    @Test
    void testGetScheduleById_NullId() {
        // Dependiendo de la implementación del repository findById, puede lanzar una excepción
        // o puede manejar el null y lanzar ScheduleNotFoundException
        assertThrows(Exception.class, () -> {
            scheduleApplicationService.getScheduleById(null);
        });

        // Si no se lanza excepción, verificaríamos si se llama al repository
        // pero como sí se lanza excepción, no podemos garantizar que se llegue a llamar
        // Así que simplemente verificamos que no se produzca una violación de contrato
    }

    @Test
    void testGetSchedulesByEmployee_NullEmployeeId() {
        assertThrows(Exception.class, () -> {
            scheduleApplicationService.getSchedulesByEmployee(null);
        });
    }


    @Test
    void testUpdateSchedule_NullId() {
        ScheduleClass updatedDetails = new ScheduleClass();
        updatedDetails.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        updatedDetails.setEndTime(LocalDateTime.of(2023, 1, 1, 18, 0));

        // Dependiendo de la implementación del repositorio, puede lanzar una excepción diferente
        assertThrows(Exception.class, () -> {
            scheduleApplicationService.updateSchedule(null, updatedDetails);
        });
    }

    @Test
    void testUpdateSchedule_NullUpdatedSchedule() {
        when(scheduleRepositoryPort.findById(10L)).thenReturn(Optional.of(schedule));

        assertThrows(NullPointerException.class, () -> {
            scheduleApplicationService.updateSchedule(10L, null);
        });

        verify(scheduleRepositoryPort, times(1)).findById(10L);
        verify(scheduleRepositoryPort, never()).save(any(ScheduleClass.class));
    }

    @Test
    void createSchedule_WithNightShift_Succeeds() {
        // Create a night shift: from 22:00 today to 06:00 tomorrow
        ScheduleClass nightShift = new ScheduleClass();
        nightShift.setEmployee(employee);
        nightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 22, 0));  // 10:00 PM
        nightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 6, 0));     // 6:00 AM next day

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.save(any(ScheduleClass.class))).thenReturn(nightShift);

        ScheduleClass result = scheduleApplicationService.createSchedule(nightShift);

        assertNotNull(result);
        assertEquals(nightShift.getStartTime(), result.getStartTime());
        assertEquals(nightShift.getEndTime(), result.getEndTime());
    }

    @Test
    void createSchedule_WithSameDayNightShift_ThrowsException() {
        // Create a night shift that ends before it starts on the same day
        ScheduleClass invalidNightShift = new ScheduleClass();
        invalidNightShift.setEmployee(employee);
        invalidNightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 22, 0));  // 10:00 PM
        invalidNightShift.setEndTime(LocalDateTime.of(2023, 1, 1, 6, 0));     // 6:00 AM same day (should fail)

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        com.employed.bar.domain.exceptions.InvalidScheduleException exception =
            assertThrows(com.employed.bar.domain.exceptions.InvalidScheduleException.class, () -> {
            scheduleApplicationService.createSchedule(invalidNightShift);
        });

        assertEquals("Start time cannot be after end time for the same day.", exception.getMessage());
    }

    @Test
    void createSchedule_WithOverlappingNightShift_ThrowsException() {
        // Existing day shift that runs late: 16:00 to 23:00
        ScheduleClass dayShift = new ScheduleClass();
        dayShift.setId(20L);
        dayShift.setEmployee(employee);
        dayShift.setStartTime(LocalDateTime.of(2023, 1, 1, 16, 0));
        dayShift.setEndTime(LocalDateTime.of(2023, 1, 1, 23, 0));

        // Try to create a night shift that overlaps at the start: 22:00 to 05:00 next day
        ScheduleClass nightShift = new ScheduleClass();
        nightShift.setEmployee(employee);
        nightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 22, 0));  // 10:00 PM
        nightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 5, 0));     // 5:00 AM next day

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.findByEmployee(employee)).thenReturn(List.of(dayShift));

        com.employed.bar.domain.exceptions.InvalidScheduleException exception =
            assertThrows(com.employed.bar.domain.exceptions.InvalidScheduleException.class, () -> {
            scheduleApplicationService.createSchedule(nightShift);
        });

        assertEquals("Schedule overlaps with an existing schedule for this employee.", exception.getMessage());
    }

    @Test
    void createSchedule_WithValidNightShiftNoOverlap_Succeeds() {
        // Create an existing day shift: 9:00 AM to 5:00 PM
        ScheduleClass dayShift = new ScheduleClass();
        dayShift.setId(20L);
        dayShift.setEmployee(employee);
        dayShift.setStartTime(LocalDateTime.of(2023, 1, 1, 9, 0));
        dayShift.setEndTime(LocalDateTime.of(2023, 1, 1, 17, 0));

        // Try to create a night shift that does not overlap: 1:00 AM to 6:00 AM next day
        ScheduleClass nightShift = new ScheduleClass();
        nightShift.setEmployee(employee);
        nightShift.setStartTime(LocalDateTime.of(2023, 1, 2, 1, 0));  // 1:00 AM next day
        nightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 6, 0));    // 6:00 AM next day

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(scheduleRepositoryPort.findByEmployee(employee)).thenReturn(List.of(dayShift));
        when(scheduleRepositoryPort.save(any(ScheduleClass.class))).thenReturn(nightShift);

                ScheduleClass result = scheduleApplicationService.createSchedule(nightShift);

        

                assertNotNull(result);

                assertEquals(nightShift.getStartTime(), result.getStartTime());

                assertEquals(nightShift.getEndTime(), result.getEndTime());

            }

        

            @Test

            void createSchedule_NightShiftVsOverlappingNightShift_ThrowsException() {

                // Existing night shift: 22:00 Jan 1 to 06:00 Jan 2

                ScheduleClass existingNightShift = new ScheduleClass();

                existingNightShift.setId(30L);

                existingNightShift.setEmployee(employee);

                existingNightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 22, 0));

                existingNightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 6, 0));

        

                // New night shift that overlaps: 23:00 Jan 1 to 07:00 Jan 2

                ScheduleClass newOverlappingNightShift = new ScheduleClass();

                newOverlappingNightShift.setEmployee(employee);

                newOverlappingNightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 23, 0));

                newOverlappingNightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 7, 0));

        

                when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

                when(scheduleRepositoryPort.findByEmployee(employee)).thenReturn(List.of(existingNightShift));

        

                com.employed.bar.domain.exceptions.InvalidScheduleException exception =

                        assertThrows(com.employed.bar.domain.exceptions.InvalidScheduleException.class, () -> {

                            scheduleApplicationService.createSchedule(newOverlappingNightShift);

                        });

        

                assertEquals("Schedule overlaps with an existing schedule for this employee.", exception.getMessage());

            }

        

            @Test

            void createSchedule_NightShiftVsOverlappingRegularShiftAtEnd_ThrowsException() {

                // Existing night shift: 22:00 Jan 1 to 06:00 Jan 2

                ScheduleClass existingNightShift = new ScheduleClass();

                existingNightShift.setId(30L);

                existingNightShift.setEmployee(employee);

                existingNightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 22, 0));

                existingNightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 6, 0));

        

                // New regular shift that overlaps at the end: 05:00 Jan 2 to 10:00 Jan 2

                ScheduleClass newOverlappingRegularShift = new ScheduleClass();

                newOverlappingRegularShift.setEmployee(employee);

                newOverlappingRegularShift.setStartTime(LocalDateTime.of(2023, 1, 2, 5, 0));

                newOverlappingRegularShift.setEndTime(LocalDateTime.of(2023, 1, 2, 10, 0));

        

                when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

                when(scheduleRepositoryPort.findByEmployee(employee)).thenReturn(List.of(existingNightShift));

        

                com.employed.bar.domain.exceptions.InvalidScheduleException exception =

                        assertThrows(com.employed.bar.domain.exceptions.InvalidScheduleException.class, () -> {

                            scheduleApplicationService.createSchedule(newOverlappingRegularShift);

                        });

        

                assertEquals("Schedule overlaps with an existing schedule for this employee.", exception.getMessage());

            }

        

            @Test

            void createSchedule_NightShiftVsOverlappingRegularShiftAtStart_ThrowsException() {

                // Existing night shift: 22:00 Jan 1 to 06:00 Jan 2

                ScheduleClass existingNightShift = new ScheduleClass();

                existingNightShift.setId(30L);

                existingNightShift.setEmployee(employee);

                existingNightShift.setStartTime(LocalDateTime.of(2023, 1, 1, 22, 0));

                existingNightShift.setEndTime(LocalDateTime.of(2023, 1, 2, 6, 0));

        

                // New regular shift that overlaps at the start: 20:00 Jan 1 to 23:00 Jan 1

                ScheduleClass newOverlappingRegularShift = new ScheduleClass();

                newOverlappingRegularShift.setEmployee(employee);

                newOverlappingRegularShift.setStartTime(LocalDateTime.of(2023, 1, 1, 20, 0));

                newOverlappingRegularShift.setEndTime(LocalDateTime.of(2023, 1, 1, 23, 0));

        

                when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

                when(scheduleRepositoryPort.findByEmployee(employee)).thenReturn(List.of(existingNightShift));

        

                com.employed.bar.domain.exceptions.InvalidScheduleException exception =

                        assertThrows(com.employed.bar.domain.exceptions.InvalidScheduleException.class, () -> {

                            scheduleApplicationService.createSchedule(newOverlappingRegularShift);

                        });

        

                assertEquals("Schedule overlaps with an existing schedule for this employee.", exception.getMessage());

            }

        }

        