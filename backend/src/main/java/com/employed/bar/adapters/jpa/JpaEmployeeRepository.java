package com.employed.bar.adapters.jpa;

import com.employed.bar.domain.model.Employee;
import com.employed.bar.ports.in.EmployeeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
@Transactional
public class JpaEmployeeRepository extends SimpleJpaRepository<Employee,Long> implements EmployeeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public JpaEmployeeRepository( EntityManager entityManager) {
        super(Employee.class, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    public Optional<Employee> findByName(String name) {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.name = :name", Employee.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Optional<Employee> findByRole(String role) {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.role = :role", Employee.class)
                .setParameter("role", role)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Employee> findByStatus(String status) {
        return entityManager.createQuery("SELECT e FROM Employee e WHERE e.status = :status", Employee.class)
                .setParameter("status", status)
                .getResultList();
    }
}
