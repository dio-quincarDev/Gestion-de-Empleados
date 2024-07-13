package com.employed.bar.adapters.jpaRepositories;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.AttendanceRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JpaAttendanceRecordRepository extends AttendanceRepository {

    @Override
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee AND ar.date = :date")
    List<AttendanceRecord> findByEmployeeAndDate(@Param("employee") Employee employee, @Param("date") LocalDate date);

    @Override
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.date = :date")
    List<AttendanceRecord> findByDate(@Param("date") LocalDate date);

    // Método adicional para buscar por empleado
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employee = :employee")
    List<AttendanceRecord> findByEmployee(@Param("employee") Employee employee);

    // Método para buscar por rango de fechas
    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



}