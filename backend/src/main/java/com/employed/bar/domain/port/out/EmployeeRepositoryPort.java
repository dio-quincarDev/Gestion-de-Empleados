package com.employed.bar.domain.port.out;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryPort {
    Optional<EmployeeClass> findByEmail(String email);
    EmployeeClass save(EmployeeClass employee);
    Optional<EmployeeClass> findById(Long id);
    List<EmployeeClass> findAll();
    List<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status);
    void delete(EmployeeClass employee);
}
