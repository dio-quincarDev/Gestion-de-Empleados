package com.employed.bar.adapters.out.persistence;
import com.employed.bar.adapters.out.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SpringEmployeeJpaRepository extends JpaRepository<EmployeeEntity, Long> {

    @Query("SELECT e FROM EmployeeEntity e WHERE e.name = :name")
    Optional<EmployeeEntity> findByName(@Param("name") String name);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.role = :role")
    Optional<EmployeeEntity> findByRole(@Param("role") String role);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.status = :status")
    List<EmployeeEntity> findByStatus(@Param("status") String status);

    Optional<EmployeeEntity> findByEmail(@Param("email") String email);

}
