package com.employed.bar.infrastructure.adapter.out.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;

public interface UserEntityRepository extends JpaRepository <UserEntity,UUID> {
	
	Optional<UserEntity>findByEmail(String email);

	List<UserEntity> findByRole(EmployeeRole role);
	

}
