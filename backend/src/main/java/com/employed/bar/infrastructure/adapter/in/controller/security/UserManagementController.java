package com.employed.bar.infrastructure.adapter.in.controller.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.security.request.CreateUserRequest;
import com.employed.bar.infrastructure.security.user.UserManagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing user accounts and roles within the application.
 * This controller provides endpoints for creating, deleting, and updating user roles,
 * with appropriate authorization checks. It acts as an inbound adapter to the application's
 * user management services.
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.USERS_ROUTE)
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserEntity createdUser = userManagementService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> updateUserRole(@PathVariable UUID id, @RequestParam EmployeeRole role) {
        userManagementService.updateUserRole(id, role);
        return ResponseEntity.noContent().build();
    }
}
