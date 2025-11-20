package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidScheduleException;
import com.employed.bar.domain.exceptions.ScheduleNotFoundException;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;
import com.employed.bar.domain.port.in.app.ScheduleUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ScheduleApplicationService implements ScheduleUseCase {
    private final ScheduleRepositoryPort scheduleRepositoryPort;
    private final EmployeeRepositoryPort employeeRepository;

    public ScheduleApplicationService(ScheduleRepositoryPort scheduleRepositoryPort, EmployeeRepositoryPort employeeRepository) {
        this.scheduleRepositoryPort = scheduleRepositoryPort;
        this.employeeRepository = employeeRepository;
    }


    @Override
    public ScheduleClass createSchedule(ScheduleClass schedule) {
        if (schedule.getEmployee() == null || schedule.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Employee ID must be set on the schedule.");
        }

        EmployeeClass employee = employeeRepository.findById(schedule.getEmployee().getId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + schedule.getEmployee().getId()));

        schedule.setEmployee(employee);

        // Para turnos nocturnos, se permite que el endTime sea menor que startTime si ocurre en el día siguiente
        // Por lo tanto, se elimina esta validación estricta
        // Se puede agregar verificación adicional si se desea limitar la duración del turno
        if (schedule.getStartTime().isAfter(schedule.getEndTime()) &&
            schedule.getStartTime().toLocalDate().isEqual(schedule.getEndTime().toLocalDate())) {
            throw new InvalidScheduleException("Start time cannot be after end time for the same day.");
        }

        // Validación opcional: limitar la duración máxima del turno (por ejemplo, no más de 24 horas)
        long hoursBetween = java.time.temporal.ChronoUnit.HOURS.between(schedule.getStartTime(), schedule.getEndTime());
        if (hoursBetween > 24 || (schedule.getStartTime().isAfter(schedule.getEndTime()) && hoursBetween < -24)) {
            throw new InvalidScheduleException("Schedule duration cannot exceed 24 hours.");
        }

        // Check for overlapping schedules - consider night shifts that cross midnight
        List<ScheduleClass> existingSchedules = scheduleRepositoryPort.findByEmployee(employee);
        for (ScheduleClass existingSchedule : existingSchedules) {
            if (schedulesOverlap(schedule, existingSchedule)) {
                throw new InvalidScheduleException("Schedule overlaps with an existing schedule for this employee.");
            }
        }

        return scheduleRepositoryPort.save(schedule);
    }

    @Override
    public ScheduleClass getScheduleById(Long id) {
        return scheduleRepositoryPort.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with ID: " + id));
    }

    @Override
    public List<ScheduleClass> getSchedulesByEmployee(Long employeeId) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
        return scheduleRepositoryPort.findByEmployee(employee);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        ScheduleClass scheduleToDelete = scheduleRepositoryPort.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with ID: " + scheduleId));
        scheduleRepositoryPort.deleteById(scheduleToDelete.getId());
    }

    @Override
    public ScheduleClass updateSchedule(Long id, ScheduleClass updatedSchedule) {
        ScheduleClass existingSchedule = scheduleRepositoryPort.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with ID: " + id));

        // Validación para turnos nocturnos
        if (updatedSchedule.getStartTime().isAfter(updatedSchedule.getEndTime()) &&
            updatedSchedule.getStartTime().toLocalDate().isEqual(updatedSchedule.getEndTime().toLocalDate())) {
            throw new InvalidScheduleException("Start time cannot be after end time for the same day.");
        }

        // Validación opcional: limitar la duración máxima del turno (por ejemplo, no más de 24 horas)
        long hoursBetween = java.time.temporal.ChronoUnit.HOURS.between(updatedSchedule.getStartTime(), updatedSchedule.getEndTime());
        if (hoursBetween > 24 || (updatedSchedule.getStartTime().isAfter(updatedSchedule.getEndTime()) && hoursBetween < -24)) {
            throw new InvalidScheduleException("Schedule duration cannot exceed 24 hours.");
        }

        // Check for overlapping schedules, excluding the schedule being updated
        EmployeeClass ownerEmployee = existingSchedule.getEmployee(); // Get the employee from the existing schedule
        List<ScheduleClass> existingSchedules = scheduleRepositoryPort.findByEmployee(ownerEmployee);
        for (ScheduleClass otherSchedule : existingSchedules) {
            if (!otherSchedule.getId().equals(id)) { // Exclude the schedule being updated
                if (schedulesOverlap(updatedSchedule, otherSchedule)) {
                    throw new InvalidScheduleException("Updated schedule overlaps with an existing schedule for this employee.");
                }
            }
        }

        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());

        if (updatedSchedule.getEmployee() != null && updatedSchedule.getEmployee().getId() != null) {
            EmployeeClass employee = employeeRepository.findById(updatedSchedule.getEmployee().getId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + updatedSchedule.getEmployee().getId()));
            existingSchedule.setEmployee(employee);
        }

        return scheduleRepositoryPort.save(existingSchedule);
    }

    /**
     * Checks if two schedules overlap, considering night shifts that cross midnight.
     * A night shift is identified when end time is on a different date than the start time,
     * or when end time is chronologically before start time (for same-date night shifts).
     */
    private boolean schedulesOverlap(ScheduleClass schedule1, ScheduleClass schedule2) {
        LocalDateTime s1Start = schedule1.getStartTime();
        LocalDateTime s1End = schedule1.getEndTime();
        LocalDateTime s2Start = schedule2.getStartTime();
        LocalDateTime s2End = schedule2.getEndTime();

        // Determine if schedules are night shifts (crossing midnight)
        boolean s1IsNightShift = s1End.isBefore(s1Start) || s1End.toLocalDate().isAfter(s1Start.toLocalDate());
        boolean s2IsNightShift = s2End.isBefore(s2Start) || s2End.toLocalDate().isAfter(s2Start.toLocalDate());

        if (s1IsNightShift) {
            if (s2IsNightShift) {
                // Both are night shifts - each spans across days
                // Check overlap by treating each as two separate periods
                return checkNightShiftOverlap(s1Start, s1End, s2Start, s2End);
            } else {
                // s1 is night shift, s2 is regular
                return checkNightWithRegularShiftOverlap(s1Start, s1End, s2Start, s2End);
            }
        } else if (s2IsNightShift) {
            // s2 is night shift, s1 is regular
            return checkNightWithRegularShiftOverlap(s2Start, s2End, s1Start, s1End);
        } else {
            // Both are regular shifts within the same day (or same day/next day non-night)
            return checkRegularShiftOverlap(s1Start, s1End, s2Start, s2End);
        }
    }

    /**
     * Check overlap between two night shifts (both cross midnight).
     */
    private boolean checkNightShiftOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        // Treat each night shift as two periods and check overlap between them
        // Night shift 1: from start1 to end of day + from start of next day to end1
        // Night shift 2: from start2 to end of day + from start of next day to end2

        // Check overlap between first shift's first day and second shift's first day
        boolean day1Overlap = checkRegularShiftOverlap(start1,
            LocalDateTime.of(start1.toLocalDate(), java.time.LocalTime.MAX),
            start2,
            LocalDateTime.of(start2.toLocalDate(), java.time.LocalTime.MAX));

        // Check overlap between first shift's second day and second shift's second day
        boolean day2Overlap = checkRegularShiftOverlap(
            LocalDateTime.of(start1.toLocalDate().plusDays(1), java.time.LocalTime.MIN).withNano(0),
            end1,
            LocalDateTime.of(start2.toLocalDate().plusDays(1), java.time.LocalTime.MIN).withNano(0),
            end2
        );

        // Check cross-day overlaps
        boolean cross12 = checkRegularShiftOverlap(start1,
            LocalDateTime.of(start1.toLocalDate(), java.time.LocalTime.MAX),
            LocalDateTime.of(start2.toLocalDate().plusDays(1), java.time.LocalTime.MIN).withNano(0),
            end2);

        boolean cross21 = checkRegularShiftOverlap(
            LocalDateTime.of(start1.toLocalDate().plusDays(1), java.time.LocalTime.MIN).withNano(0),
            end1,
            start2,
            LocalDateTime.of(start2.toLocalDate(), java.time.LocalTime.MAX));

        return day1Overlap || day2Overlap || cross12 || cross21;
    }

    /**
     * Check overlap between a night shift and a regular shift.
     */
    private boolean checkNightWithRegularShiftOverlap(LocalDateTime nightStart, LocalDateTime nightEnd,
                                                    LocalDateTime regularStart, LocalDateTime regularEnd) {
        // Night shift: from nightStart to end of day + from start of next day to nightEnd
        // Regular shift: from regularStart to regularEnd (all in same day/next day)

        // Check if regular shift overlaps with first part of night shift (same day)
        boolean sameDayOverlap = checkRegularShiftOverlap(nightStart,
            LocalDateTime.of(nightStart.toLocalDate(), java.time.LocalTime.MAX),
            regularStart, regularEnd);

        // Check if regular shift overlaps with second part of night shift (next day)
        boolean nextDayOverlap = checkRegularShiftOverlap(
            LocalDateTime.of(nightStart.toLocalDate().plusDays(1), java.time.LocalTime.MIN).withNano(0),
            nightEnd,
            regularStart, regularEnd);

        return sameDayOverlap || nextDayOverlap;
    }

    /**
     * Standard overlap check for regular shifts.
     */
    private boolean checkRegularShiftOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        // Standard interval overlap: [start1, end1] overlaps [start2, end2] if start1 < end2 and start2 < end1
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}