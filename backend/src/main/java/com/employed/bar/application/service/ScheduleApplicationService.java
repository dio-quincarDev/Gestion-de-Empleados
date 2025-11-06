package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidScheduleException;
import com.employed.bar.domain.exceptions.ScheduleNotFoundException;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;
import com.employed.bar.domain.port.in.app.ScheduleUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;

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

        if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
            throw new InvalidScheduleException("Start time cannot be after end time.");
        }

        // Check for overlapping schedules
        List<ScheduleClass> existingSchedules = scheduleRepositoryPort.findByEmployee(employee);
        for (ScheduleClass existingSchedule : existingSchedules) {
            if (schedule.getStartTime().isBefore(existingSchedule.getEndTime()) &&
                    schedule.getEndTime().isAfter(existingSchedule.getStartTime())) {
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

        if (updatedSchedule.getStartTime().isAfter(updatedSchedule.getEndTime())) {
            throw new InvalidScheduleException("Start time cannot be after end time.");
        }

        // Check for overlapping schedules, excluding the schedule being updated
        EmployeeClass ownerEmployee = existingSchedule.getEmployee(); // Get the employee from the existing schedule
        List<ScheduleClass> existingSchedules = scheduleRepositoryPort.findByEmployee(ownerEmployee);
        for (ScheduleClass otherSchedule : existingSchedules) {
            if (!otherSchedule.getId().equals(id)) { // Exclude the schedule being updated
                if (updatedSchedule.getStartTime().isBefore(otherSchedule.getEndTime()) &&
                        updatedSchedule.getEndTime().isAfter(otherSchedule.getStartTime())) {
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
}