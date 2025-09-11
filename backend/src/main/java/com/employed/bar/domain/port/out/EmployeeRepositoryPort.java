package com.employed.bar.domain.port.out;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryPort {
    Optional<EmployeeClass> findByEmail(String email);
    EmployeeClass save(EmployeeClass employee);
    Optional<EmployeeClass> findById(Long id);
    List<EmployeeClass> findByStatus(String status);
    Optional<EmployeeClass> findByName(String name);
    Optional<EmployeeClass> findByRole(EmployeeRole role);
    List<EmployeeClass> findAll();
    void delete(EmployeeClass employee);
}
