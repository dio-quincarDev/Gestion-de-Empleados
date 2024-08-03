package com.employed.bar.adapters.jpa;

import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.ConsumptionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class JpaConsumptionRepository extends SimpleJpaRepository<Consumption, Long> implements ConsumptionRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public JpaConsumptionRepository(EntityManager entityManager) {
        super(Consumption.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<Consumption> findByEmployeeAndDateTimeBetween(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager.createQuery(
                        "SELECT c FROM Consumption c WHERE c.employee = :employee AND c.consumptionDate BETWEEN :startDate AND :endDate",
                        Consumption.class)
                .setParameter("employee", employee)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public List<Consumption> findByEmployee(Employee employee) {
        return entityManager.createQuery(
                        "SELECT c FROM Consumption c WHERE c.employee = :employee",
                        Consumption.class)
                .setParameter("employee", employee)
                .getResultList();
    }

    @Override
    public Double sumConsumptionByEmployeeAndDateRange(Employee employee, LocalDateTime startDate, LocalDateTime endDate) {
        return entityManager.createQuery(
                        "SELECT SUM(c.amount) FROM Consumption c WHERE c.employee = :employee AND c.consumptionDate BETWEEN :startDate AND :endDate",
                        Double.class)
                .setParameter("employee", employee)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
    }

    // Otros métodos de JpaRepository ya están implementados por SimpleJpaRepository
}