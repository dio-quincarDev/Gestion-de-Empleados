package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceUseCase {

    AttendanceRecordClass registerAttendance(AttendanceRecordClass attendanceRecord);

    List<AttendanceRecordClass> findEmployeeAttendances(Long employeeId, LocalDate date);

    double calculateAttendancePercentage(Long employeeId, int year, int month, int day);

    List<AttendanceRecordClass> getAttendanceListByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);
}
