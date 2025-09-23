package com.employed.bar.domain.port.in.app;

import com.employed.bar.domain.model.structure.ScheduleClass;

import java.util.List;

public interface ScheduleUseCase {
    ScheduleClass createSchedule(ScheduleClass schedule);
    ScheduleClass getScheduleById(Long id);
    List<ScheduleClass> getSchedulesByEmployee(Long employeeId);
    void deleteSchedule(Long scheduleId);
    ScheduleClass updateSchedule(Long id, ScheduleClass updatedSchedule);
}
