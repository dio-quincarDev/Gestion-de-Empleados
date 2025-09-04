package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;

import java.util.List;

public interface ScheduleRepositoryPort {
    List<Schedule> findByEmployee(Employee employee);
}
