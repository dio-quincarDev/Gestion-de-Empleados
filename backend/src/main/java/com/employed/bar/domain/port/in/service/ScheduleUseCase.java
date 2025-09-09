package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.ScheduleClass;
import com.employed.bar.infrastructure.dto.ScheduleDto;

import java.util.List;

public interface ScheduleUseCase {
    ScheduleClass createSchedule(ScheduleDto scheduleDto);
    ScheduleClass getScheduleById(Long id);
    List<ScheduleClass> getSchedulesByEmployee(Long employeeId);
    void deleteSchedule(Long scheduleId);
    ScheduleClass updateSchedule(Long id, ScheduleClass updatedSchedule);
}
