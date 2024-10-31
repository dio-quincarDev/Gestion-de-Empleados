package com.employed.bar.ports.in;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsumptionRepository extends JpaRepository <Consumption, Long> {
    @Query("SELECT c FROM Consumption c WHERE c.employee = :employee AND c.consumptionDate BETWEEN :startDate AND :endDate AND (:description IS NULL OR c.description LIKE %:description%)")
    List<Consumption> findByEmployeeAndDateTimeBetween(@Param("employee") Employee employee,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate,
                                                       @Param("description") String description);


    @Query("SELECT SUM(c.amount) FROM Consumption c WHERE c.employee = :employee AND c.consumptionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumConsumptionByEmployeeAndDateRange(@Param("employee")Employee employee,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(c.amount) FROM Consumption c WHERE c.consumptionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalConsumptionByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

}
