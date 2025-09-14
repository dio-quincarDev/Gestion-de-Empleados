package com.employed.bar.infrastructure.adapter.out.persistence.repository;
import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SpringEmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long>, JpaSpecificationExecutor<EmployeeEntity> {

    Optional<EmployeeEntity> findByEmail(@Param("email") String email);

}
