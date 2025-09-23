package com.employed.bar.infrastructure.adapter.in.controller.security;

import com.employed.bar.domain.enums.EmployeeRole;
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

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequest request) {
        userManagementService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> updateUserRole(@PathVariable UUID id, @RequestParam EmployeeRole role) {
        userManagementService.updateUserRole(id, role);
        return ResponseEntity.noContent().build();
    }
}
