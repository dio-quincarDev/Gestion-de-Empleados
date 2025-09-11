package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.in.service.ConsumptionUseCase;
import com.employed.bar.domain.port.out.ConsumptionRepository;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.dto.domain.ConsumptionDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsumptionApplicationService implements ConsumptionUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final ConsumptionRepository consumptionRepository;

    public ConsumptionClass processConsumption(ConsumptionDto consumptionDto) {
        EmployeeClass employee = employeeRepository.findById(consumptionDto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + consumptionDto.getEmployeeId()));

        ConsumptionClass consumptionClass = new ConsumptionClass();
        consumptionClass.setEmployee(employee);
        consumptionClass.setAmount(consumptionDto.getAmount());
        consumptionClass.setConsumptionDate(consumptionDto.getDate());
        consumptionClass.setDescription(consumptionDto.getDescription());

        return createConsumption(consumptionClass);
    }

    @Override
    public ConsumptionClass createConsumption(ConsumptionClass consumptionClass) {
        return consumptionRepository.save(consumptionClass);
    }

    @Override
    public Optional<ConsumptionClass> getConsumptionById(Long id) {
        return consumptionRepository.findById(id);
    }

    @Override
    public List<ConsumptionClass> getConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate,
                                                                LocalDateTime endDate, String description) {
        return consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, description);
    }

    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionRepository.sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate);
    }
    
    public BigDecimal calculateTotalConsumptionByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return calculateTotalConsumptionByEmployee(employee, startDateTime, endDateTime);
    }

    @Override
    public BigDecimal calculateTotalConsumptionForAllEmployees(LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionRepository.sumTotalConsumptionByDateRange(startDate, endDate);
    }

    @Override
    public void deleteConsumption(Long id) {
        consumptionRepository.deleteById(id);
    }
}