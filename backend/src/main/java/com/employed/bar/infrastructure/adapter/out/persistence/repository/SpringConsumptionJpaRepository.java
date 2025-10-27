package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import com.employed.bar.infrastructure.adapter.out.persistence.entity.ConsumptionEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpringConsumptionJpaRepository extends JpaRepository<ConsumptionEntity, Long> {

    @Query("SELECT c FROM ConsumptionEntity c JOIN FETCH c.employee WHERE c.employee.id = :employeeId AND c.consumptionDate BETWEEN :startDate AND :endDate AND (:description IS NULL OR c.description LIKE %:description%)")
    List<ConsumptionEntity> findByEmployeeAndDateTimeBetween(@Param("employeeId") Long employeeId,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate,
                                                            @Param("description") String description);


    @Query("SELECT SUM(c.amount) FROM ConsumptionEntity c WHERE c.employee = :employee AND c.consumptionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumConsumptionByEmployeeAndDateRange(@Param("employee") EmployeeEntity employee,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(c.amount) FROM ConsumptionEntity c WHERE c.consumptionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalConsumptionByDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

}
