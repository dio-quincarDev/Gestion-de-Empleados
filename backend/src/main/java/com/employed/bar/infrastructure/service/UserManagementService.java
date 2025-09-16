package com.employed.bar.infrastructure.service;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.dto.security.CreateUserRequest;

import java.util.UUID;

public interface UserManagementService {

    UserEntity createUser(CreateUserRequest request);

    void deleteUser(UUID id);

    void updateUserRole(UUID id, EmployeeRole role);
}
