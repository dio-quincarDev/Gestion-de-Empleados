package com.employed.bar.domain.services;

import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsumptionService {

    Consumption createConsumption(Consumption consumption);

    Optional<Consumption> getConsumptionById(Long id);

    List<ConsumptionReportDto> getConsumptionByEmployee(Employee employee, LocalDateTime startDate,
                                                        LocalDateTime endDate, String description);



    BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal calculateTotalConsumptionForAllEmployees(LocalDateTime startDate, LocalDateTime endDate);

    void deleteConsumption(Long id);


}
