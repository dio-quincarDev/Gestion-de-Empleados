package com.employed.bar.domain.servicesImpl;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
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
public class ConsumptionServiceImpl implements ConsumptionService {
    private final ConsumptionRepository consumptionRepository;

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
        return consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate);
    }

    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate){
       BigDecimal totalConsumption = BigDecimal.ZERO;
       List<Consumption> consumptions = getConsumptionByEmployee(employee, startDate, endDate);
       for (Consumption consumption : consumptions) {
           totalConsumption = totalConsumption.add(consumption.getAmount());
       }
        return totalConsumption;
    }

    @Override
    public void deleteConsumption(Long id){
        consumptionRepository.deleteById(id);
    }
}
