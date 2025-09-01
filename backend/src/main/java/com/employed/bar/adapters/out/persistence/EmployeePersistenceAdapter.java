package com.employed.bar.adapters.out.persistence;

import com.employed.bar.adapters.out.persistence.entity.EmployeeEntity;
import com.employed.bar.adapters.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.out.EmployeeRepositoryPort;
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
    public Optional<Employee> findByEmail(String email) {
        return springEmployeeJpaRepository.findByEmail(email)
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        EmployeeEntity savedEntity = springEmployeeJpaRepository.save(employeeEntity);
        return employeeMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return springEmployeeJpaRepository.findById(id)
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public List<Employee> findByStatus(String status) {
        return springEmployeeJpaRepository.findByStatus(status).stream()
                .map(entity -> employeeMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Employee> findByName(String name) {
        return springEmployeeJpaRepository.findByName(name)
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public Optional<Employee> findByRole(String role) {
        return springEmployeeJpaRepository.findByRole(role)
                .map(entity -> employeeMapper.toDomain(entity));
    }

    @Override
    public List<Employee> findAll() {
        return springEmployeeJpaRepository.findAll().stream()
                .map(entity -> employeeMapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Employee employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        springEmployeeJpaRepository.delete(employeeEntity);
    }
}
