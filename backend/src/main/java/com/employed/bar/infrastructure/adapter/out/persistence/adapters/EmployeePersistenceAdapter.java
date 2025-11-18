package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.EmployeeSpecification;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public Page<EmployeeClass> findAll(Pageable pageable) {
        return springEmployeeJpaRepository.findAll(EmployeeSpecification.isActiveOrInactive(), pageable)
                .map(employeeMapper::toDomain);
    }

    @Override
    public void delete(EmployeeClass employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        springEmployeeJpaRepository.delete(employeeEntity);
    }

    @Override
    public Page<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status, Pageable pageable) {
        Specification<EmployeeEntity> spec = EmployeeSpecification.isActiveOrInactive();
        if (name != null) {
            spec = spec.and(EmployeeSpecification.nameContains(name));
        }
        if (role != null) {
            spec = spec.and(EmployeeSpecification.hasRole(role));
        }
        if (status != null) {
            spec = spec.and(EmployeeSpecification.hasStatus(status));
        }
        return springEmployeeJpaRepository.findAll(spec, pageable)
                .map(employeeMapper::toDomain);
    }
}
