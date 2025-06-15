package com.employed.bar.ports.in;
import com.employed.bar.domain.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.name = :name")
    Optional<Employee> findByName(@Param("name") String name);

    @Query("SELECT e FROM Employee e WHERE e.role = :role")
    Optional<Employee> findByRole(@Param("role") String role);

    @Query("SELECT e FROM Employee e WHERE e.status = :status")
    List<Employee> findByStatus(@Param("status") String status);

    Optional<Employee> findByEmail(@Param("email") String email);

}
