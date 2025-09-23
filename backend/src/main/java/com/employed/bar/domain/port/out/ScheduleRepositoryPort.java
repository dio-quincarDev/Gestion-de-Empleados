package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

public interface ScheduleRepositoryPort {
    ScheduleClass save(ScheduleClass schedule);
    List<ScheduleClass> findByEmployee(EmployeeClass employee);
    List<ScheduleClass> findByEmployeeAndDate(EmployeeClass employee, LocalDateTime startTime, LocalDateTime endTime);
    List<ScheduleClass> findByDateRange(LocalDateTime startTime, LocalDateTime endTime);

    void deleteById(Long scheduleId);
    Optional<ScheduleClass> findById(Long id);
}