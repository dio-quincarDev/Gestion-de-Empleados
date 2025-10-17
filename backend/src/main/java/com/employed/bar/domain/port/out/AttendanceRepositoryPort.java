package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.structure.AttendanceRecordClass;
import com.employed.bar.domain.model.structure.EmployeeClass;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepositoryPort {
    AttendanceRecordClass save(AttendanceRecordClass attendanceRecordClass);
    List<AttendanceRecordClass> findByEmployeeAndDateRange(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate);
    List<AttendanceRecordClass> findByEmployee(EmployeeClass employee);
}
