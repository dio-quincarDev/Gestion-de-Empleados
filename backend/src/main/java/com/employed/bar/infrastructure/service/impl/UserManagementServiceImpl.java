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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity createUser(CreateUserRequest request) {
        UserEntity authenticatedUser = getAuthenticatedUser();

        if (request.getRole().ordinal() >= authenticatedUser.getRole().ordinal()) {
            throw new SecurityException("Cannot create a user with a role equal to or higher than your own.");
        }

        if (request.getRole() == EmployeeRole.MANAGER && userEntityRepository.existsByRole(EmployeeRole.MANAGER)) {
            throw new IllegalStateException("A MANAGER already exists in the system.");
        }

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

        if (authenticatedUser.getId().equals(userToDelete.getId())) {
            throw new SecurityException("Users cannot delete themselves.");
        }

        if (userToDelete.getRole().ordinal() >= authenticatedUser.getRole().ordinal()) {
            throw new SecurityException("Cannot delete a user with a role equal to or higher than your own.");
        }

        userEntityRepository.delete(userToDelete);
    }

    @Override
    public void updateUserRole(UUID id, EmployeeRole role) {
        UserEntity userToUpdate = userEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserEntity authenticatedUser = getAuthenticatedUser();

        if (authenticatedUser.getId().equals(userToUpdate.getId())) {
            throw new SecurityException("Users cannot change their own role.");
        }

        if (userToUpdate.getRole().ordinal() >= authenticatedUser.getRole().ordinal()) {
            throw new SecurityException("Cannot update a user with a role equal to or higher than your own.");
        }

        if (role.ordinal() >= authenticatedUser.getRole().ordinal()) {
            throw new SecurityException("Cannot assign a role that is equal to or higher than your own.");
        }

        if (role == EmployeeRole.MANAGER) {
            Optional<UserEntity> manager = userEntityRepository.findByRole(EmployeeRole.MANAGER);
            if (manager.isPresent() && !manager.get().getId().equals(id)) {
                throw new IllegalStateException("A MANAGER already exists in the system.");
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
