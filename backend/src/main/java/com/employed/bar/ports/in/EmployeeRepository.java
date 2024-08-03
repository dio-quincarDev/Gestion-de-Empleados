package com.employed.bar.ports.in;
import com.employed.bar.domain.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    Optional<Employee> findByName(String name);
    Optional<Employee> findByRole(String role);
    List<Employee> findByStatus(String status);

}
