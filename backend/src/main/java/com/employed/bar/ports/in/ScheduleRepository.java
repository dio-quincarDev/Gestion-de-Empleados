package com.employed.bar.ports.in;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByEmployee(Employee employee);
    Schedule findByEmployeeAndDate(Employee employee, LocalDate date);
}
