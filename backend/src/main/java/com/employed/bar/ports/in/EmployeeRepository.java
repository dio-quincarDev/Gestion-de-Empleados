package com.employed.bar.ports.in;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee employee);

    List<AttendanceRecord> findAttendanceRecordsByEmployeeAndDate(Employee employee, int year, int month, int day);

    Optional<Employee> findById(Long id);

    List<Employee> findAll();

    void delete(Employee employee);

    Schedule findByEmployeeAndDate(Employee employee, int year, int month, int day);

    List<AttendanceRecord> findAttendanceRecords(int year, int month, int day);
}
