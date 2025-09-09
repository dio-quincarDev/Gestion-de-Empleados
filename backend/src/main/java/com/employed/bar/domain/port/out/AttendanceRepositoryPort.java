package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.EmployeeClass;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryPort {
    AttendanceRecord save(AttendanceRecord attendanceRecord);
    List<AttendanceRecord> findByEmployeeAndDateRange(EmployeeClass employee, LocalDate startDate, LocalDate endDate);
    List<AttendanceRecord> findByEmployee(EmployeeClass employee);
}
