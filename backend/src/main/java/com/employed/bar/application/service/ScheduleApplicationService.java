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
     * Checks if two schedules overlap. This works for any schedule, including those that cross midnight,
     * as long as LocalDateTime is used consistently.
     * The logic is that two intervals [start1, end1] and [start2, end2] overlap if, and only if,
     * start1 is before end2 AND start2 is before end1.
     */
    private boolean schedulesOverlap(ScheduleClass schedule1, ScheduleClass schedule2) {
        LocalDateTime start1 = schedule1.getStartTime();
        LocalDateTime end1 = schedule1.getEndTime();
        LocalDateTime start2 = schedule2.getStartTime();
        LocalDateTime end2 = schedule2.getEndTime();

        // This simple check correctly handles all cases, including day, night, and multi-day shifts.
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}