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
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {

    // Consulta por empleado y fecha
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee AND ar.date = :date")
    List<AttendanceRecord> findByEmployeeAndDate(@Param("employee") Employee employee, @Param("date") LocalDate date);

    // Consulta por fecha específica
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.date = :date")
    List<AttendanceRecord> findByDate(@Param("date") LocalDate date);

    // Consulta por año, mes y día
    @Query("SELECT ar FROM AttendanceRecord ar WHERE YEAR(ar.date) = :year AND MONTH(ar.date) = :month AND DAY(ar.date) = :day")
    List<AttendanceRecord> findAttendanceRecords(@Param("year") int year, @Param("month") int month, @Param("day") int day);

    // Consulta por empleado, año, mes y día
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee AND YEAR(ar.date) = :year AND MONTH(ar.date) = :month AND DAY(ar.date) = :day")
    List<AttendanceRecord> findAttendanceRecordsByEmployeeAndDate(@Param("employee") Employee employee,
                                                                  @Param("year") int year,
                                                                  @Param("month") int month,
                                                                  @Param("day") int day);

    // Consulta adicional por empleado
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee")
    List<AttendanceRecord> findByEmployee(@Param("employee") Employee employee);

    // Consulta por rango de fechas
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee AND ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByEmployeeAndDateRange(
            @Param("employee") Employee employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
