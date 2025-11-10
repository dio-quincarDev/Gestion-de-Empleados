package com.employed.bar.domain.port.in.app;

import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.domain.model.structure.EmployeeClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsumptionUseCase {

    ConsumptionClass createConsumption(ConsumptionClass consumptionClass);

    Optional<ConsumptionClass> getConsumptionById(Long id);

    List<ConsumptionClass> getConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate,
                                                        LocalDateTime endDate, String description);

    BigDecimal calculateTotalConsumptionByEmployee(EmployeeClass employee, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal calculateTotalConsumptionForAllEmployees(LocalDateTime startDate, LocalDateTime endDate);

    void deleteConsumption(Long id);

    List<ConsumptionClass> getAllConsumptions();

    ConsumptionClass updateConsumption(ConsumptionClass consumptionClass);


}