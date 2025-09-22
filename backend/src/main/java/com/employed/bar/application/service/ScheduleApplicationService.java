package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
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
        return scheduleRepositoryPort.save(schedule);
    }

    @Override
    public ScheduleClass getScheduleById(Long id) {
        Optional<ScheduleClass> scheduleOptional = scheduleRepositoryPort.findById(id);
        return scheduleOptional.orElse(null);

    }

    @Override
    public List<ScheduleClass> getSchedulesByEmployee(Long employeeId) {
        EmployeeClass employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee != null) {
            return scheduleRepositoryPort.findByEmployee(employee);
        }
        return Collections.emptyList();

    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepositoryPort.deleteById(scheduleId);
    }

    @Override
    public ScheduleClass updateSchedule(Long id, ScheduleClass updatedSchedule) {
        ScheduleClass existingSchedule = scheduleRepositoryPort.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with ID: " + id));

        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());
        return scheduleRepositoryPort.save(existingSchedule);
    }
}
