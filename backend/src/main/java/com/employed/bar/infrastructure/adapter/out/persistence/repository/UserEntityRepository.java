package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository <UserEntity,UUID> {
	
	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByRole(EmployeeRole role);

	boolean existsByRole(EmployeeRole role);

}
