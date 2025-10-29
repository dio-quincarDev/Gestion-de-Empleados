package com.employed.bar.domain.port.out;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.structure.EmployeeClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeRepositoryPort {
    Optional<EmployeeClass> findByEmail(String email);
    EmployeeClass save(EmployeeClass employee);
    Optional<EmployeeClass> findById(Long id);
    Page<EmployeeClass> findAll(Pageable pageable);
    Page<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status, Pageable pageable);
    void delete(EmployeeClass employee);
}
