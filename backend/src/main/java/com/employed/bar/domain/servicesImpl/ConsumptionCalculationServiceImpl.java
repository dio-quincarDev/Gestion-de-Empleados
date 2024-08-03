package com.employed.bar.domain.servicesImpl;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ConsumptionCalculationService;
import com.employed.bar.ports.in.ConsumptionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsumptionCalculationServiceImpl implements ConsumptionCalculationService {
    private final ConsumptionRepository consumptionRepository;

    public ConsumptionCalculationServiceImpl(ConsumptionRepository consumptionRepository) {
        this.consumptionRepository = consumptionRepository;

    }

    //Logica para calcular Consumo de cada empleado
    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalConsumption = BigDecimal.ZERO;
        List<Consumption>consumptions = getConsumptionByEmployee(employee, startDate, endDate);
        for (Consumption consumption : consumptions) {
            totalConsumption = totalConsumption.add(consumption.getAmount());
        }
        return totalConsumption;
    }

    @Override
    public List<Consumption> getConsumptionsByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public List<Consumption> getConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate);
    }



}
