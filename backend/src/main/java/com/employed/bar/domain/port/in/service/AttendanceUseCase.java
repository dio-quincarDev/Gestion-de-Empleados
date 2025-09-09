package com.employed.bar.domain.port.in.service;

import com.employed.bar.infrastructure.dto.AttendanceDto;
import com.employed.bar.infrastructure.dto.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.EmployeeClass;

import java.time.LocalDate;

import java.util.List;

public interface AttendanceUseCase {

    AttendanceRecord registerAttendance(AttendanceDto attendanceDto);
    List<AttendanceRecord>findEmployeeAttendances(EmployeeClass employee, LocalDate date);
    double calculateAttendancePercentage(EmployeeClass employee, int year, int month, int day);
    List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day);
    List<AttendanceRecord> getAttendanceListByEmployeeAndDateRange(EmployeeClass employee, LocalDate startDate, LocalDate endDate);
}
