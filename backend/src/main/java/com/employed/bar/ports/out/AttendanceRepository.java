package com.employed.bar.ports.out;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord,Long>{

    List<AttendanceRecord> findByEmployeeAndDate(Employee employee, LocalDate date);

    List<AttendanceRecord> findByDate(LocalDate date);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE YEAR(ar.date) = :year AND MONTH(ar.date) = :month AND DAY(ar.date) = :day")
    List<AttendanceRecord> findAttendanceRecords(@Param("year") int year,
                                                 @Param("month") int month,
                                                 @Param("day") int day);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee AND YEAR(ar.date) = :year AND MONTH(ar.date) = :month AND DAY(ar.date) = :day")
    List<AttendanceRecord> findAttendanceRecordsByEmployeeAndDate(@Param("employee") Employee employee,
                                                                  @Param("year") int year,
                                                                  @Param("month") int month,
                                                                  @Param("day") int day);

    List<AttendanceRecord> findByEmployee(Employee employee);
}
