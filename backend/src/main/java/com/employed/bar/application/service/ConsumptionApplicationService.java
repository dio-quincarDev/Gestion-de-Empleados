package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.ConsumptionUseCase;
import com.employed.bar.domain.port.out.ConsumptionRepositoryPort;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ConsumptionApplicationService implements ConsumptionUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final ConsumptionRepositoryPort consumptionRepositoryPort;

    public ConsumptionApplicationService(EmployeeRepositoryPort employeeRepository, ConsumptionRepositoryPort consumptionRepositoryPort) {
        this.employeeRepository = employeeRepository;
        this.consumptionRepositoryPort = consumptionRepositoryPort;
    }


    @Override
    public ConsumptionClass createConsumption(ConsumptionClass consumptionClass) {
        EmployeeClass employee = employeeRepository.findById(consumptionClass.getEmployee().getId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + consumptionClass.getEmployee().getId()));
        consumptionClass.setEmployee(employee);
        return consumptionRepositoryPort.save(consumptionClass);
    }

    @Override
    public Optional<ConsumptionClass> getConsumptionById(Long id) {
        return consumptionRepositoryPort.findById(id);
    }

    @Override
    public List<ConsumptionClass> getConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate,
                                                                LocalDateTime endDate, String description) {
        return consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, description);
    }

    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionRepositoryPort.sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate);
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
        return consumptionRepositoryPort.sumTotalConsumptionByDateRange(startDate, endDate);
    }

    @Override
    public void deleteConsumption(Long id) {
        consumptionRepositoryPort.deleteById(id);
    }
}