package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.payment.*;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.PaymentDetailEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.EmployeeSpecification;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringEmployeeJpaRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Transactional(readOnly = true)
    public Optional<EmployeeClass> findByEmail(String email) {
        return springEmployeeJpaRepository.findByEmail(email)
                .map(employeeMapper::toDomain);
    }

    @Override
    @Transactional
    public EmployeeClass save(EmployeeClass employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        // Manejar la colección de paymentDetails fuera del mapper para evitar problemas con all-delete-orphan
        updatePaymentDetails(employeeEntity, employee.getPaymentMethod());
        EmployeeEntity savedEntity = springEmployeeJpaRepository.save(employeeEntity);
        // Recuperar el empleado con los detalles de pago para asegurar que estén disponibles
        // para el mapeo correcto del paymentMethod
        return springEmployeeJpaRepository.findById(savedEntity.getId())
                .map(employeeMapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Error retrieving saved employee: " + savedEntity.getId()));
    }

    private void updatePaymentDetails(EmployeeEntity employeeEntity, PaymentMethod paymentMethod) {
        // Limpiar la colección existente
        if (employeeEntity.getPaymentDetails() != null) {
            employeeEntity.getPaymentDetails().clear();
        } else {
            employeeEntity.setPaymentDetails(new ArrayList<>());
        }

        // Si no hay paymentMethod, solo limpiar
        if (paymentMethod == null) {
            return;
        }

        // Crear y añadir el nuevo PaymentDetailEntity
        PaymentDetailEntity pde = getPaymentDetail(employeeEntity, paymentMethod);
        // Para Cash, no se requieren campos adicionales

        employeeEntity.getPaymentDetails().add(pde);
    }

    @Transactional(readOnly = true)
    private static @NotNull PaymentDetailEntity getPaymentDetail(EmployeeEntity employeeEntity, PaymentMethod paymentMethod) {
        PaymentDetailEntity pde = new PaymentDetailEntity();
        pde.setEmployee(employeeEntity);
        pde.setDefault(true);
        pde.setPaymentMethodType(paymentMethod.getType());

        if (paymentMethod instanceof AchPaymentMethod ach) {
            pde.setBankName(ach.getBankName());
            pde.setAccountNumber(ach.getAccountNumber());
            pde.setBankAccountType(ach.getBankAccountType());
        } else if (paymentMethod instanceof YappyPaymentMethod yappy) {
            pde.setPhoneNumber(yappy.getPhoneNumber());
        }
        return pde;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeClass> findById(Long id) {
        return springEmployeeJpaRepository.findById(id)
                .map(employeeMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeClass> findAll(Pageable pageable) {
        return springEmployeeJpaRepository.findAll(pageable)
                .map(employee -> {
                    // Cargar manualmente el empleado con paymentDetails para el mapeo
                    Long employeeId = employee.getId();
                    if (employeeId != null) {
                        return springEmployeeJpaRepository.findById(employeeId)
                                .map(employeeMapper::toDomain)
                                .orElse(employeeMapper.toDomain(employee));
                    }
                    return employeeMapper.toDomain(employee);
                });
    }

    @Override
    @Transactional
    public void delete(EmployeeClass employee) {
        EmployeeEntity employeeEntity = employeeMapper.toEntity(employee);
        springEmployeeJpaRepository.delete(employeeEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeClass> searchEmployees(String name, EmployeeRole role, EmployeeStatus status, Pageable pageable) {
        Specification<EmployeeEntity> spec = Specification.where(null); // No initial spec que haga fetch
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
                .map(employee -> {
                    // Cargar manualmente el empleado con paymentDetails para el mapeo
                    Long employeeId = employee.getId();
                    if (employeeId != null) {
                        return springEmployeeJpaRepository.findById(employeeId)
                                .map(employeeMapper::toDomain)
                                .orElse(employeeMapper.toDomain(employee));
                    }
                    return employeeMapper.toDomain(employee);
                });
    }
}
