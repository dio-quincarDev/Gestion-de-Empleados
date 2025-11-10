package com.employed.bar.application.service;

import com.employed.bar.domain.exceptions.ConsumptionNotFoundException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidDateRangeException;
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
        if (consumptionClass.getEmployee() == null) {
            throw new IllegalArgumentException("Employee cannot be null for consumption creation.");
        }
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
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date cannot be after end date");
        }

        List<ConsumptionClass> consumptions = consumptionRepositoryPort.findByEmployeeAndDateTimeBetween(
                employee, startDate, endDate, description);

        // Ordenar por fecha descendente (más reciente primero)
        consumptions.sort((c1, c2) -> c2.getConsumptionDate().compareTo(c1.getConsumptionDate()));

        return consumptions;
    }

    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionRepositoryPort.sumConsumptionByEmployeeAndDateRange(employee.getId(), startDate, endDate);
    }
    
    public BigDecimal calculateTotalConsumptionByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) {
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date cannot be after end date");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return calculateTotalConsumptionByEmployee(employee, startDateTime, endDateTime);
    }

    @Override
    public BigDecimal calculateTotalConsumptionForAllEmployees(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }

        BigDecimal total = consumptionRepositoryPort.sumTotalConsumptionByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public void deleteConsumption(Long id) {
        if (!consumptionRepositoryPort.findById(id).isPresent()) {
            throw new ConsumptionNotFoundException("Consumption not found with id " + id);
        }
        consumptionRepositoryPort.deleteById(id);
    }

    @Override
    public List<ConsumptionClass> getAllConsumptions() {
        return consumptionRepositoryPort.findAll();
    }

    @Override
    public ConsumptionClass updateConsumption(ConsumptionClass consumptionClass) {
        if (consumptionClass.getEmployee() == null) {
            throw new IllegalArgumentException("Employee cannot be null for consumption update.");
        }
        return consumptionRepositoryPort.findById(consumptionClass.getId())
                .map(existingConsumption -> {
                    EmployeeClass employee = employeeRepository.findById(consumptionClass.getEmployee().getId())
                            .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + consumptionClass.getEmployee().getId()));
                    existingConsumption.setEmployee(employee);
                    existingConsumption.setAmount(consumptionClass.getAmount());
                    existingConsumption.setDescription(consumptionClass.getDescription());
                    existingConsumption.setConsumptionDate(consumptionClass.getConsumptionDate());
                    return consumptionRepositoryPort.save(existingConsumption);
                })
                .orElseThrow(() -> new ConsumptionNotFoundException("Consumption not found with id " + consumptionClass.getId()));
    }
}