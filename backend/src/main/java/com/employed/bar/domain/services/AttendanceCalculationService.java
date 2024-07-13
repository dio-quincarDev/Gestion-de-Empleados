package com.employed.bar.domain.services;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface AttendanceCalculationService {

        long calculateTotalWorkingMinutes(@NotNull List<Schedule> schedules);

        boolean isOnSchedule(@NotNull Employee employee, @NotNull LocalDateTime dateTime);

        boolean isWithinSchedule(@NotNull Schedule schedule, @NotNull LocalDateTime dateTime);

        double calculateAttendancePercentage(Employee employee, int year, int month, int day);

        List<AttendanceReportDto>generateAttendanceReport(int year, int month, int day);
}
