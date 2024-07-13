package com.employed.bar.domain.services;

import com.employed.bar.adapters.dtos.AttendanceDto;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

AttendanceRecord registerAttendance(AttendanceDto attendanceDto);
List<AttendanceRecord>findEmployeeAttendances(Employee employee, LocalDate date);
double calculateAtendancePercentege(Employee employee, int year, int month, int day);

}
