package com.employed.bar.application;

import com.employed.bar.adapters.dtos.ScheduleDto;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.ports.out.EmployeeRepositoryPort;
import com.employed.bar.ports.in.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleApplicationService {
    private final ScheduleRepository scheduleRepository;
    private final EmployeeRepositoryPort employeeRepository;

    public Schedule createSchedule(ScheduleDto scheduleDto){
        Employee employee = employeeRepository.findById(scheduleDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Schedule schedule = new Schedule();
        schedule.setStartTime(scheduleDto.getStartTime());
        schedule.setEndTime(scheduleDto.getEndTime());
        schedule.setEmployee(employee);

        return scheduleRepository.save(schedule);
    }

    public Schedule getScheduleById(Long id){
        return scheduleRepository.findById(id).orElse(null);

    }
    public List<Schedule> getSchedulesByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if(employee != null) {
            return scheduleRepository.findByEmployee(employee);
        }
        return Collections.emptyList();

    }

    public void deleteSchedule(Long scheduleId){
        scheduleRepository.deleteById(scheduleId);
    }

    public Schedule updateSchedule(Long id, Schedule updatedSchedule) {
        Schedule existingSchedule = getScheduleById(id);
        existingSchedule.setStartTime(updatedSchedule.getStartTime());
        existingSchedule.setEndTime(updatedSchedule.getEndTime());
        return scheduleRepository.save(existingSchedule);
    }
}
