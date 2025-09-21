package com.employed.bar.infrastructure.adapter.out.persistence.adapters;

import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.model.structure.ScheduleClass;
import com.employed.bar.domain.port.out.ScheduleRepositoryPort;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.ScheduleEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.ScheduleMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.mapper.EmployeeMapper;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.SpringScheduleJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SchedulePersistenceAdapter implements ScheduleRepositoryPort {

    private final SpringScheduleJpaRepository springScheduleJpaRepository;
    private final ScheduleMapper scheduleMapper;
    private final EmployeeMapper employeeMapper;

    public SchedulePersistenceAdapter(SpringScheduleJpaRepository springScheduleJpaRepository, ScheduleMapper scheduleMapper, EmployeeMapper employeeMapper) {
        this.springScheduleJpaRepository = springScheduleJpaRepository;
        this.scheduleMapper = scheduleMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public ScheduleClass save(ScheduleClass schedule) {
        ScheduleEntity scheduleEntity = scheduleMapper.toEntity(schedule);
        ScheduleEntity savedEntity = springScheduleJpaRepository.save(scheduleEntity);
        return scheduleMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ScheduleClass> findById(Long id) {
        return springScheduleJpaRepository.findById(id)
                .map(scheduleMapper::toDomain);
    }

    @Override
    public List<ScheduleClass> findByEmployee(EmployeeClass employee) {
        return springScheduleJpaRepository.findByEmployee(employeeMapper.toEntity(employee)).stream()
                .map(scheduleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleClass> findByEmployeeAndDate(EmployeeClass employee, LocalDateTime startTime, LocalDateTime endTime) {
        return springScheduleJpaRepository.findByEmployeeAndDate(employeeMapper.toEntity(employee), startTime, endTime).stream()
                .map(scheduleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleClass> findByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return springScheduleJpaRepository.findByDateRange(startTime, endTime).stream()
                .map(scheduleMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long scheduleId) {
        springScheduleJpaRepository.deleteById(scheduleId);
    }
}
