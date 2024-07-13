package com.employed.bar.application;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.ports.in.EmployeeRepository;
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
    private final EmployeeRepository employeeRepository;

    public Schedule createSchedule(Schedule schedule){
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
