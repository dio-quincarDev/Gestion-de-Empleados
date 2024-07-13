package com.employed.bar.domain.servicesImpl;

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
public class ConsumptionServiceImpl implements ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final EmployeeRepository employeeRepository;
    private final ConsumptionCalculationService consumptionCalculationService;

    public ConsumptionServiceImpl(ConsumptionRepository consumptionRepository, EmployeeRepository employeeRepository, ConsumptionCalculationService consumptionCalculationService) {
        this.consumptionRepository = consumptionRepository;
        this.employeeRepository = employeeRepository;
        this.consumptionCalculationService = consumptionCalculationService;
    }

    @Override
    public Consumption createConsumption(Consumption consumption) {
        return consumptionRepository.save(consumption);
    }

    @Override
    public Optional<Consumption> getConsumptionById(Long id){
        return consumptionRepository.findById(id);
    }

    @Override
    public List<Consumption> getConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate){
        return consumptionCalculationService.getConsumptionByEmployee(employee, startDate, endDate);
    }

    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate){
        return consumptionCalculationService.calculateTotalConsumptionByEmployee(employee, startDate, endDate);
    }

    @Override
    public void deleteConsumption(Long id){
        consumptionRepository.deleteById(id);
    }
}
