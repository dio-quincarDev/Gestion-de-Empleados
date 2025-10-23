package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import com.employed.bar.infrastructure.adapter.out.persistence.entity.AttendanceRecordEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringAttendanceJpaRepository extends JpaRepository<AttendanceRecordEntity, Long> {

    @Query("SELECT ar FROM AttendanceRecordEntity ar JOIN FETCH ar.employee WHERE ar.employee.id = :employeeId AND ar.entryDateTime BETWEEN :startDateTime AND :endDateTime")
    List<AttendanceRecordEntity> findByEmployeeAndEntryDateTimeBetween(@Param("employeeId") Long employeeId, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    List<AttendanceRecordEntity> findByEmployee(EmployeeEntity employee);
}
