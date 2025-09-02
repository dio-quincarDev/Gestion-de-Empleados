package com.employed.bar.domain.ports.in.service;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeUseCase {
    Employee createEmployee(Employee employee);
    Optional<Employee> getEmployeeById(Long id);
    Optional<Employee> getEmployeeByName(String name);
    Optional<Employee> getEmployeeByRole(EmployeeRole role);
    List<Employee> getEmployeeByStatus(String status);
    List<Employee> getEmployees( );
    void deleteEmployee(Long id);
    Employee updateEmployee(Long id, Employee updatedEmployee);
    Optional<Employee> findByEmail(String email);
}
