package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.util.List;
import java.util.Optional;

public interface EmployeeUseCase {
    EmployeeClass createEmployee(EmployeeClass employee);
    Optional<EmployeeClass> getEmployeeById(Long id);
    List<EmployeeClass> getEmployees( );
    List<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status);
    void deleteEmployee(Long id);
    EmployeeClass updateEmployee(Long id, EmployeeClass updatedEmployee);
    Optional<EmployeeClass> findByEmail(String email);
    EmployeeClass updateHourlyRate(Long employeeId, java.math.BigDecimal newRate);
    java.math.BigDecimal calculateEmployeePay(Long employeeId, double regularHours, double overtimeHours);
}
