package com.employed.bar.adapters.out.persistence.mapper;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.adapters.out.persistence.entity.EmployeeEntity;

public class EmployeeMapper {

    public static EmployeeEntity toEntity(Employee domain) {
        if (domain == null) {
            return null;
        }
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setRole(domain.getRole());
        entity.setSalary(domain.getSalary());
        entity.setStatus(domain.getStatus());
        // Relationships (schedules, attendanceRecords, consumptions) will need separate mappers or handling
        // For now, we'll keep it simple.
        return entity;
    }

    public static Employee toDomain(EmployeeEntity entity) {
        if (entity == null) {
            return null;
        }
        Employee domain = new Employee();
        domain.setId(entity.getId());
        domain.setName(entity.getName());
        domain.setEmail(entity.getEmail());
        domain.setRole(entity.getRole());
        domain.setSalary(entity.getSalary());
        domain.setStatus(entity.getStatus());
        // Relationships (schedules, attendanceRecords, consumptions) will need separate mappers or handling
        // For now, we'll keep it simple.
        return domain;
    }
}
