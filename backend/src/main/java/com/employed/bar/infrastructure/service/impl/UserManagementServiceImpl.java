package com.employed.bar.infrastructure.service.impl;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.infrastructure.dto.security.CreateUserRequest;
import com.employed.bar.infrastructure.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity createUser(CreateUserRequest request) {
        if (userEntityRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already in use: " + request.getEmail());
        }

        UserEntity userToSave = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(request.getRole())
                .build();

        return userEntityRepository.save(userToSave);
    }

    @Override
    public void deleteUser(UUID id) {
        UserEntity userToDelete = userEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserEntity authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == EmployeeRole.ADMIN) {
            if (userToDelete.getRole() == EmployeeRole.MANAGER || userToDelete.getRole() == EmployeeRole.ADMIN) {
                throw new SecurityException("An ADMIN cannot delete a MANAGER or another ADMIN.");
            }
        }

        userEntityRepository.delete(userToDelete);
    }

    @Override
    public void updateUserRole(UUID id, EmployeeRole role) {
        UserEntity userToUpdate = userEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserEntity authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getRole() == EmployeeRole.ADMIN) {
            if (role == EmployeeRole.MANAGER) {
                throw new SecurityException("An ADMIN cannot assign the MANAGER role.");
            }
        }

        userToUpdate.setRole(role);
        userEntityRepository.save(userToUpdate);
    }

    private UserEntity getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userEntityRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));
    }
}
