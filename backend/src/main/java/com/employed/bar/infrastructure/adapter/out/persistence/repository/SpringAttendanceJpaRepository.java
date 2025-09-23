package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SpringAttendanceJpaRepository extends JpaRepository<AttendanceRecordEntity, Long> {

    @Query("SELECT ar FROM AttendanceRecordEntity ar JOIN FETCH ar.employee WHERE ar.employee = :employee AND ar.date BETWEEN :startDate AND :endDate")
    List<AttendanceRecordEntity> findByEmployeeAndDateBetween(@Param("employee") EmployeeEntity employee, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<AttendanceRecordEntity> findByEmployee(EmployeeEntity employee);
}
