package com.employed.bar.domain.services;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsumptionCalculationService {
    BigDecimal calculateTotalConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate);
    List<Consumption> getConsumptionsByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate);
    List<Consumption> getConsumptionByEmployee(Employee employee, LocalDateTime startDate, LocalDateTime endDate);
}
