package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import com.employed.bar.infrastructure.adapter.out.persistence.entity.ScheduleEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByEmployee(EmployeeEntity employee);

    @Query("SELECT s FROM ScheduleEntity s JOIN FETCH s.employee WHERE s.employee = :employee AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<ScheduleEntity> findByEmployeeAndDate(
            @Param("employee") EmployeeEntity employee,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM ScheduleEntity s JOIN FETCH s.employee WHERE s.startTime >= :startTime AND s.endTime <= :endTime")
    List<ScheduleEntity> findByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
