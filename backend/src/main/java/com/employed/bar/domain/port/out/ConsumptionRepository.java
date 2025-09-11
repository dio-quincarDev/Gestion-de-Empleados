package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.strucuture.ConsumptionClass;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsumptionRepository {

    ConsumptionClass save(ConsumptionClass consumptionClass);

    Optional<ConsumptionClass> findById(Long id);

    void deleteById(Long id);

    List<ConsumptionClass> findByEmployeeAndDateTimeBetween(EmployeeClass employee,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate,
                                                            String description);

    BigDecimal sumConsumptionByEmployeeAndDateRange(EmployeeClass employee,
                                                    LocalDateTime startDate,
                                                    LocalDateTime endDate);

    BigDecimal sumTotalConsumptionByDateRange(LocalDateTime startDate,
                                              LocalDateTime endDate);

    List<ConsumptionClass> findAll();
}