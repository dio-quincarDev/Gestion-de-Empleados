package com.employed.bar.infrastructure.adapter.in.controller.security;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.security.user.EmployeeRoleChangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller especializado para manejar la lógica de cambio de roles de empleados
 * Específicamente para crear usuarios ADMIN cuando se promueve un empleado
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.USERS_ROUTE + "/promotion")
@RequiredArgsConstructor
@Tag(name = "User Promotion", description = "Endpoints for promoting employees to user roles")
public class EmployeePromotionController {

    private final EmployeeRoleChangeService employeeRoleChangeService;

    @PostMapping("/employee/{employeeId}/to-admin")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @Operation(summary = "Promote employee to admin", description = "Creates a user account with ADMIN role for an existing employee. Only accessible by users with the MANAGER role.")
    public ResponseEntity<Void> promoteEmployeeToAdmin(@PathVariable Long employeeId, @Valid @RequestBody PasswordRequest passwordRequest) {
        employeeRoleChangeService.handleRoleChange(employeeId, EmployeeRole.ADMIN, passwordRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PasswordRequest {
        @NotBlank(message = "Password is mandatory")
        private String password;
    }
}