package com.employed.bar.application;

import com.employed.bar.adapters.dtos.ConsumptionDto;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ConsumptionCalculationService;
import com.employed.bar.domain.services.ConsumptionService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsumptionApplicationService {

    private final ConsumptionService consumptionService;
    private final EmployeeRepository employeeRepository;
    private final ConsumptionCalculationService consumptionCalculationService;
    private final ConsumptionRepository consumptionRepository;




    public Consumption processConsumption(ConsumptionDto consumptionDto) {
        Employee employee = employeeRepository.findById(consumptionDto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + consumptionDto.getEmployeeId()));

        // Mapea los datos del DTO a la entidad Consumption
        Consumption consumption = new Consumption();
        consumption.setEmployee(employee);
        consumption.setAmount(consumptionDto.getAmount());
        consumption.setConsumptionDate(consumptionDto.getDate());

        return consumptionRepository.save(consumption);
    }

    public Consumption createConsumption(Consumption consumption) {
        return consumptionRepository.save(consumption);
    }

    public Optional<Consumption> getConsumptionById(Long id) {
        return consumptionService.getConsumptionById(id);
    }

    public List<Consumption> getConsumptionsByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionCalculationService.getConsumptionsByEmployee(employee, startDate, endDate);
    }

    public BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionCalculationService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);
    }

    public void deleteConsumption(Long id) {
        consumptionRepository.deleteById(id);
    }
}
