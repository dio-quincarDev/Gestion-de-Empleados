package com.employed.bar.infrastructure.adapter.out.persistence.repository;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SpringEmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long>, JpaSpecificationExecutor<EmployeeEntity> {

    @Query("SELECT e FROM EmployeeEntity e LEFT JOIN FETCH e.paymentDetails WHERE e.id = :id")
    Optional<EmployeeEntity> findById(@Param("id") Long id);

    Page<EmployeeEntity> findAll(Pageable pageable);

    @Query("SELECT e FROM EmployeeEntity e LEFT JOIN FETCH e.paymentDetails WHERE e.email = :email")
    Optional<EmployeeEntity> findByEmail(@Param("email") String email);

}
