package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ConsumptionEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.ConsumptionMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringConsumptionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsumptionPersistenceAdapterPort implements ConsumptionRepositoryPort {

    private final SpringConsumptionJpaRepository springConsumptionJpaRepository;
    private final ConsumptionMapper consumptionMapper;
    private final EmployeeMapper employeeMapper;

    @Override
    public ConsumptionClass save(ConsumptionClass consumptionClass) {
        ConsumptionEntity consumptionEntity = consumptionMapper.toEntity(consumptionClass);
        ConsumptionEntity savedEntity = springConsumptionJpaRepository.save(consumptionEntity);
        return consumptionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ConsumptionClass> findById(Long id) {
        return springConsumptionJpaRepository.findById(id)
                .map(consumptionMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        springConsumptionJpaRepository.deleteById(id);
    }

    @Override
    public List<ConsumptionClass> findByEmployeeAndDateTimeBetween(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate, String description) {
        return springConsumptionJpaRepository.findByEmployeeAndDateTimeBetween(
                employeeMapper.toEntity(employee), startDate, endDate, description)
                .stream()
                .map(consumptionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal sumConsumptionByEmployeeAndDateRange(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate) {
        return springConsumptionJpaRepository.sumConsumptionByEmployeeAndDateRange(
                employeeMapper.toEntity(employee), startDate, endDate);
    }

    @Override
    public BigDecimal sumTotalConsumptionByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return springConsumptionJpaRepository.sumTotalConsumptionByDateRange(startDate, endDate);
    }

    @Override
    public List<ConsumptionClass> findAll() {
        return springConsumptionJpaRepository.findAll().stream()
                .map(consumptionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
