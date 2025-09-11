package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
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
                .map(entity -> employeeMapper.toDomain(entity));
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
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public List<EmployeeClass> findByStatus(String status) {
        return springEmployeeJpaRepository.findByStatus(status).stream()
                .map(entity -> employeeMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeClass> findByName(String name) {
        return springEmployeeJpaRepository.findByName(name)
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public Optional<EmployeeClass> findByRole(EmployeeRole role) {
        return springEmployeeJpaRepository.findByRole(role)
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public List<EmployeeClass> findAll() {
        return springEmployeeJpaRepository.findAll().stream()
                .map(entity -> employeeMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(EmployeeClass employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        springEmployeeJpaRepository.delete(employeeEntity);
    }
}
