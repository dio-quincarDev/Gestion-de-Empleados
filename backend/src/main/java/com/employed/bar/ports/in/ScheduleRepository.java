package com.employed.bar.ports.in;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByEmployee(Employee employee);

    @Query("SELECT s FROM Schedule s WHERE s.employee = :employee AND s.startTime >= :startTime AND s.endTime <= :endTime")
    List<Schedule> findByEmployeeAndDate(
            @Param("employee") Employee employee,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM Schedule s WHERE s.startTime >= :startTime AND s.endTime <= :endTime")
    List<Schedule> findByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

}
