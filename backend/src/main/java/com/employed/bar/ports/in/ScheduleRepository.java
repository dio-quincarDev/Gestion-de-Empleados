package com.employed.bar.ports.in;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);

    Schedule findById(Long id);

    @Query("SELECT s FROM Schedule s")
    List<Schedule> findAll();

    void deleteById(Long id);


    List<Schedule> findByEmployee(Employee employee);
}
