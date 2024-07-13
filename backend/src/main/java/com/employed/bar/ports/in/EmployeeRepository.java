package com.employed.bar.ports.in;

import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    Optional<Employee> findByName(String name);
    Optional<Employee> findByRole(String role);
    List<Employee> findByStatus(String status);

}
