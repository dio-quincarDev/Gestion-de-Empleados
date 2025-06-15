package com.employed.bar.domain.services;

import com.employed.bar.domain.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee createEmployee(Employee employee);
    Optional<Employee> getEmployeeById(Long id);
    Optional<Employee> getEmployeeByName(String name);
    Optional<Employee> getEmployeeByRole(String role);
    List<Employee> getEmployeeByStatus(String status);
    List<Employee> getEmployees( );
    void deleteEmployee(Long id);
    Employee updateEmployee(Long id, Employee updatedEmployee);
    Optional<Employee> findByEmail(String email);
}
