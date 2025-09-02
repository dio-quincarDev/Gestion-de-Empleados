package com.employed.bar.infrastructure.adapter.out.persistence.mapper;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.dtos.EmployeeDto;

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

    public static Employee toDomain(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        Employee domain = new Employee();
        domain.setId(dto.getId());
        domain.setName(dto.getName());
        domain.setEmail(dto.getEmail());
        domain.setRole(dto.getRole());
        domain.setSalary(dto.getSalary());
        domain.setStatus(dto.getStatus());
        return domain;
    }

    public static EmployeeDto toDto(Employee domain) {
        if (domain == null) {
            return null;
        }
        EmployeeDto dto = new EmployeeDto();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setEmail(domain.getEmail());
        dto.setRole(domain.getRole());
        dto.setSalary(domain.getSalary());
        dto.setStatus(domain.getStatus());
        return dto;
    }
}
