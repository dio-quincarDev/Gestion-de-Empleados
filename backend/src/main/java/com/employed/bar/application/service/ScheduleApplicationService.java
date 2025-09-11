package com.employed.bar.application.service;

import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.model.strucuture.ScheduleClass;
import com.employed.bar.domain.port.in.service.ScheduleUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleApplicationService implements ScheduleUseCase {
    private final ScheduleRepositoryPort scheduleRepositoryPort;
    private final EmployeeRepositoryPort employeeRepository;

    @Override
    public ScheduleClass createSchedule(ScheduleClass schedule) {
        if (schedule.getEmployee() == null || schedule.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Employee must be set on the schedule.");
        }
        // Ensure the employee exists
        employeeRepository.findById(schedule.getEmployee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + schedule.getEmployee().getId()));

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
        ScheduleClass existingSchedule = getScheduleById(id);
        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());
        return scheduleRepositoryPort.save(existingSchedule);
    }
}
