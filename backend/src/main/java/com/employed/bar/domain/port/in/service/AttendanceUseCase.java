package com.employed.bar.domain.port.in.service;

import com.employed.bar.infrastructure.dto.AttendanceDto;
import com.employed.bar.infrastructure.dto.AttendanceReportDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDate;

import java.util.List;

public interface AttendanceUseCase {

    AttendanceRecord registerAttendance(AttendanceDto attendanceDto);
    List<AttendanceRecord>findEmployeeAttendances(Employee employee, LocalDate date);
    double calculateAttendancePercentage(Employee employee, int year, int month, int day);
    List<AttendanceReportDto> generateAttendanceReport(int year, int month, int day);
    List<AttendanceRecord> getAttendanceListByEmployeeAndDateRange(Employee employee, LocalDate startDate, LocalDate endDate);
}
