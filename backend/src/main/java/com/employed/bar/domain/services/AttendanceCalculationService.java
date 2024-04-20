package com.employed.bar.domain.services;

import com.employed.bar.domain.exceptions.InvalidScheduleException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
@Service
public interface AttendanceCalculationService {

    default long calculateTotalWorkingMinutes(@NotNull List<Schedule> schedules) {
        return schedules.stream()
                .mapToLong(this::calculateWorkingMinutesForSchedule)
                .sum();
    }

    private long calculateWorkingMinutesForSchedule(@NotNull Schedule schedule) {
        LocalDateTime start = schedule.getStartTime();
        LocalDateTime end = schedule.getEndTime();
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new InvalidScheduleException("End time must be after start time");
        }
        return Duration.between(start, end).toMinutes();
    }

    boolean isOnSchedule(@NotNull Employee employee, @NotNull LocalDateTime dateTime);

    boolean isWithinSchedule(@NotNull Schedule schedule, @NotNull LocalDateTime dateTime);
}
