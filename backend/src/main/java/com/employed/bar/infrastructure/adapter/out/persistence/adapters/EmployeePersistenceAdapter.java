package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.EmployeeSpecification;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EmployeePersistenceAdapter implements EmployeeRepositoryPort {

    private final SpringEmployeeJpaRepository springEmployeeJpaRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeePersistenceAdapter(SpringEmployeeJpaRepository springEmployeeJpaRepository, EmployeeMapper employeeMapper) {
        this.springEmployeeJpaRepository = springEmployeeJpaRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Optional<EmployeeClass> findByEmail(String email) {
        return springEmployeeJpaRepository.findByEmail(email)
                .map(employeeMapper::toDomain);
    }

    @Override
    public EmployeeClass save(EmployeeClass employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        EmployeeEntity savedEntity = springEmployeeJpaRepository.save(employeeEntity);
        return employeeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<EmployeeClass> findById(Long id) {
        return springEmployeeJpaRepository.findById(id)
                .map(employeeMapper::toDomain);
    }

    @Override
    public List<EmployeeClass> findAll() {
        return springEmployeeJpaRepository.findAll().stream()
                .map(employeeMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(EmployeeClass employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        springEmployeeJpaRepository.delete(employeeEntity);
    }

    @Override
    public List<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status) {
        Specification<EmployeeEntity> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(EmployeeSpecification.nameContains(name));
        }
        if (role != null) {
            spec = spec.and(EmployeeSpecification.hasRole(role));
        }
        if (status != null) {
            spec = spec.and(EmployeeSpecification.hasStatus(status));
        }
        return springEmployeeJpaRepository.findAll(spec).stream()
                .map(employeeMapper::toDomain)
                .collect(Collectors.toList());
    }
}
