package com.employed.bar.ports.in;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord,Long>{
    List<AttendanceRecord> findByEmployeeAndDate(Employee employee, LocalDate date);
    List<AttendanceRecord> findByDate(LocalDate date);
    List<AttendanceRecord> findAttendanceRecords(int year, int month, int day);
    List<AttendanceRecord> findAttendanceRecordsByEmployeeAndDate(Employee employee, int year, int month, int day);
    List<AttendanceRecord> findByEmployee(Employee employee);
}
