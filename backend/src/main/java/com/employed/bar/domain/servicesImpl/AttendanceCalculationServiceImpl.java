package com.employed.bar.domain.servicesImpl;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.exceptions.InvalidScheduleException;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import com.employed.bar.domain.services.AttendanceCalculationService;
import com.employed.bar.ports.in.AttendanceRepository;
import com.employed.bar.ports.in.EmployeeRepository;

import com.employed.bar.ports.in.ScheduleRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceCalculationServiceImpl implements AttendanceCalculationService {
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;

    public AttendanceCalculationServiceImpl(EmployeeRepository employeeRepository,
                                            AttendanceRepository attendanceRepository,
                                            ScheduleRepository scheduleRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.scheduleRepository = scheduleRepository;
    }
    @Override
    public long calculateTotalWorkingMinutes(@NotNull List<Schedule> schedules) {
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

    @Override
    public boolean isOnSchedule(@NotNull Employee employee, @NotNull LocalDateTime dateTime) {
        List<Schedule> schedules = scheduleRepository.findByEmployee(employee);
        return schedules.stream()
                .anyMatch(schedule -> isWithinSchedule(schedule, dateTime));
    }

    @Override
    public boolean isWithinSchedule(@NotNull Schedule schedule, @NotNull LocalDateTime dateTime) {
        return !dateTime.isBefore(schedule.getStartTime()) && !dateTime.isAfter(schedule.getEndTime());
    }

    @Override
    public double calculateAttendancePercentage(Employee employee, int year, int month, int day) {
        if (year < 1 || month > 12 || day < 1 || day > 31) {
            throw new IllegalArgumentException("Invalid date");
        }
        List<AttendanceRecord> records = attendanceRepository.findAttendanceRecords( year, month, day);
        long totalWorkingMinutes = 0;
        for (AttendanceRecord record : records) {
                long entryMinute = record.getEntryTime().getHour() * 60 + record.getEntryTime().getMinute();
                long exitMinute = record.getExitTime().getHour() * 60 + record.getExitTime().getMinute();
                totalWorkingMinutes += exitMinute - entryMinute;
            }
        Schedule schedule = scheduleRepository.findByEmployeeAndDate( employee, LocalDate.of(year, month, day));
        if (schedule == null) {
            return 0.0;
        }
        long scheduleMinutes = Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes();
        if (scheduleMinutes == 0) {
            return 0.0;
        }
        return (double) totalWorkingMinutes / scheduleMinutes;
    }

    @Override
    public List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day) {
        return List.of();
    }
}


