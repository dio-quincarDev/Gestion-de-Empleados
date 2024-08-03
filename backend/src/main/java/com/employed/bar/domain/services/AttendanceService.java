package com.employed.bar.domain.services;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceService {

    AttendanceRecord registerAttendance(AttendanceDto attendanceDto);
    List<AttendanceRecord>findEmployeeAttendances(Employee employee, LocalDate date);
    double calculateAttendancePercentage(Employee employee, int year, int month, int day);
    long calculateTotalWorkingMinutes(@NotNull List <Schedule> schedules);

    boolean isOnSchedule(@NotNull Employee employee, @NotNull LocalDateTime dateTime);

    boolean isWithinSchedule(@NotNull Schedule schedule, @NotNull LocalDateTime dateTime);

    List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day);

}
