package com.employed.bar.application.service;

import com.employed.bar.infrastructure.dto.ScheduleDto;
import com.employed.bar.domain.model.EmployeeClass;
import com.employed.bar.domain.model.ScheduleClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;
import com.employed.bar.domain.port.in.service.ScheduleUseCase;
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
    public ScheduleClass createSchedule(ScheduleDto scheduleDto){
        EmployeeClass employee = employeeRepository.findById(scheduleDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        ScheduleClass schedule = new ScheduleClass();
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setEmployee(employee);

        return scheduleRepositoryPort.save(schedule);
    }

    @Override
    public ScheduleClass getScheduleById(Long id){
        Optional<ScheduleClass> scheduleOptional =scheduleRepositoryPort.findById(id);
        if (scheduleOptional.isPresent()){
            return scheduleOptional.get();
        }else{
            return null;
        }

    }
    @Override
    public List<ScheduleClass> getSchedulesByEmployee(Long employeeId) {
        EmployeeClass employee = employeeRepository.findById(employeeId).orElse(null);
        if(employee != null) {
            return scheduleRepositoryPort.findByEmployee(employee);
        }
        return Collections.emptyList();

    }

    @Override
    public void deleteSchedule(Long scheduleId){
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