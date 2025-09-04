package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryPort {
    AttendanceRecord save(AttendanceRecord attendanceRecord);
    List<AttendanceRecord> findByEmployeeAndDateRange(Employee employee, LocalDate startDate, LocalDate endDate);
    List<AttendanceRecord> findByEmployee(Employee employee);
}
