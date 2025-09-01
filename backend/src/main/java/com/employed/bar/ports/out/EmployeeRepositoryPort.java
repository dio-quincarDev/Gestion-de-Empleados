package com.employed.bar.ports.out;

import com.employed.bar.domain.model.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryPort {
    Optional<Employee> findByEmail(String email);
    Employee save(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findByStatus(String status);
    Optional<Employee> findByName(String name);
    Optional<Employee> findByRole(String role);
    List<Employee> findAll();
    void delete(Employee employee);
}
