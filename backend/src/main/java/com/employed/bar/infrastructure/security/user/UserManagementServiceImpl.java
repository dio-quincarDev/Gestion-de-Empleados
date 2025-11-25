package com.employed.bar.infrastructure.security.user;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.adapter.out.persistence.repository.UserEntityRepository;
import com.employed.bar.domain.exceptions.UserNotFoundException;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserEntity createUser(CreateUserRequest request) {
        // The controller's @PreAuthorize("hasRole('MANAGER')") ensures only a MANAGER can execute this.

        if (request.getRole() == EmployeeRole.MANAGER) {
            // A MANAGER cannot create another MANAGER.
            throw new SecurityException("Cannot create a user with the MANAGER role.");
        }

        if (userEntityRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already in use: " + request.getEmail());
        }

        UserEntity userToSave = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .build();

        return userEntityRepository.save(userToSave);
    }

    @Override
    public void deleteUser(UUID id) {
        UserEntity userToDelete = userEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserEntity authenticatedUser = getAuthenticatedUser();

        // Rule: Users cannot delete themselves.
        if (authenticatedUser.getId().equals(userToDelete.getId())) {
            throw new SecurityException("Users cannot delete themselves.");
        }

        // A MANAGER can delete anyone (except themselves, checked above).
        // An ADMIN has restrictions.
        if (authenticatedUser.getRole() == EmployeeRole.ADMIN) {
            // Rule: An ADMIN cannot delete a MANAGER.
            if (userToDelete.getRole() == EmployeeRole.MANAGER) {
                throw new SecurityException("An ADMIN cannot delete a MANAGER.");
            }
            // Rule: An ADMIN cannot delete another ADMIN.
            if (userToDelete.getRole() == EmployeeRole.ADMIN) {
                throw new SecurityException("An ADMIN cannot delete another ADMIN.");
            }
        }

        userEntityRepository.delete(userToDelete);
    }

    @Override
    public void updateUserRole(UUID id, EmployeeRole role) {
        UserEntity userToUpdate = userEntityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        UserEntity authenticatedUser = getAuthenticatedUser();

        // Rule: Users cannot change their own role.
        if (authenticatedUser.getId().equals(userToUpdate.getId())) {
            throw new SecurityException("Users cannot change their own role.");
        }

        // A MANAGER can change anyone's role (except their own, checked above).
        // An ADMIN has restrictions.
        if (authenticatedUser.getRole() == EmployeeRole.ADMIN) {
            // Rule: An ADMIN cannot change a MANAGER's role.
            if (userToUpdate.getRole() == EmployeeRole.MANAGER) {
                throw new SecurityException("An ADMIN cannot change a MANAGER's role.");
            }
            // Rule: An ADMIN cannot change another ADMIN's role.
            if (userToUpdate.getRole() == EmployeeRole.ADMIN) {
                throw new SecurityException("An ADMIN cannot change another ADMIN's role.");
            }
            // Rule: An ADMIN cannot promote anyone to MANAGER.
            if (role == EmployeeRole.MANAGER) {
                throw new SecurityException("An ADMIN cannot promote users to MANAGER.");
            }
            // Rule: An ADMIN cannot promote anyone to ADMIN.
            if (role == EmployeeRole.ADMIN) {
                throw new SecurityException("An ADMIN cannot promote users to ADMIN.");
            }
        }

        // General rule: Nobody can assign the MANAGER role if one already exists.
        if (role == EmployeeRole.MANAGER) {
            Optional<UserEntity> manager = userEntityRepository.findByRole(EmployeeRole.MANAGER);
            if (manager.isPresent() && !manager.get().getId().equals(id)) {
                throw new IllegalStateException("A MANAGER already exists in the system and you cannot assign this role to another user.");
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
