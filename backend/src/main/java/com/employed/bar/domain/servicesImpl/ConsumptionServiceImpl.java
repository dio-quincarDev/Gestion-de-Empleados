package com.employed.bar.domain.servicesImpl;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ConsumptionService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<Consumption> getConsumptionById(Long id) {
        return consumptionRepository.findById(id);
    }

    @Override
    public List<ConsumptionReportDto> getConsumptionByEmployee(Employee employee, LocalDateTime startDate,
                                                               LocalDateTime endDate, String description) {

        return consumptionRepository.findByEmployeeAndDateTimeBetween(employee, startDate, endDate, description)
                .stream()
                .map(consumption -> new ConsumptionReportDto(
                        consumption.getEmployee().getName(),
                        consumption.getConsumptionDate(),
                        consumption.getAmount(),
                        consumption.getDescription()))

                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return consumptionRepository.sumConsumptionByEmployeeAndDateRange(employee, startDate, endDate);
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