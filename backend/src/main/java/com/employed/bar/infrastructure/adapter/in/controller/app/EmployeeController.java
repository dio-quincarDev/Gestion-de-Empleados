package com.employed.bar.infrastructure.adapter.in.controller.app;

import com.employed.bar.domain.enums.EmployeeRole;
import com.employed.bar.domain.enums.EmployeeStatus;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.app.EmployeeUseCase;
import com.employed.bar.infrastructure.adapter.in.mapper.EmployeeApiMapper;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.EmployeeDto;
import com.employed.bar.infrastructure.dto.request.UpdateHourlyRateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing employee information.
 * This controller handles HTTP requests related to employee CRUD operations, hourly rate updates,
 * and employee searches. It acts as an inbound adapter to the application's core employee management functionalities.
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.EMPLOYEE_ROUTE)
@Tag(name = "1. Gestión de Empleados", description = "Endpoints para la administración del personal")
public class EmployeeController {
    private final EmployeeUseCase employeeUseCase;
    private final EmployeeApiMapper employeeApiMapper;

    public EmployeeController(EmployeeUseCase employeeUseCase, EmployeeApiMapper employeeApiMapper) {
        this.employeeUseCase = employeeUseCase;
        this.employeeApiMapper = employeeApiMapper;
    }

    @Operation(
            summary = "Crear nuevo empleado",
            description = "Registra un nuevo empleado en el sistema",
            operationId = "createEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado creado exitosamente",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya está registrado"
            )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody @Valid EmployeeDto employeeDto) {
        EmployeeClass employeeToCreate = employeeApiMapper.toDomain(employeeDto);
        EmployeeClass createdEmployee = employeeUseCase.createEmployee(employeeToCreate);
        return new ResponseEntity<>(employeeApiMapper.toDto(createdEmployee), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Obtener empleado por ID",
            description = "Recupera la información completa de un empleado específico",
            operationId = "getEmployeeById"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado encontrado",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado"
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        return employeeUseCase.getEmployeeById(id)
                .map(employee -> ResponseEntity.ok(employeeApiMapper.toDto(employee)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Listar todos los empleados",
            description = "Obtiene una lista completa de todos los empleados registrados",
            operationId = "getAllEmployees"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de empleados obtenida exitosamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDto.class)))
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        List<EmployeeDto> employees = employeeUseCase.getEmployees().stream()
                .map(employeeApiMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(
            summary = "Actualizar empleado",
            description = "Modifica la información de un empleado existente",
            operationId = "updateEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El nuevo email ya está registrado"
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeDto employeeDto) {
        EmployeeClass employeeToUpdate = employeeApiMapper.toDomain(employeeDto);
        EmployeeClass updatedEmployee = employeeUseCase.updateEmployee(id, employeeToUpdate);
        return ResponseEntity.ok(employeeApiMapper.toDto(updatedEmployee));
    }

    @Operation(
            summary = "Actualizar tarifa por hora de un empleado",
            description = "Modifica parcialmente la tarifa por hora de un empleado específico.",
            operationId = "updateHourlyRate"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarifa por hora actualizada exitosamente", content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PatchMapping("/{id}/hourly-rate")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<EmployeeDto> updateHourlyRate(@PathVariable Long id, @RequestBody @Valid UpdateHourlyRateRequest request) {
        EmployeeClass updatedEmployee = employeeUseCase.updateHourlyRate(id, request.getHourlyRate());
        return ResponseEntity.ok(employeeApiMapper.toDto(updatedEmployee));
    }

    @Operation(
            summary = "Eliminar empleado",
            description = "Remueve un empleado del sistema",
            operationId = "deleteEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Empleado eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeUseCase.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar empleados",
            description = "Busca empleados por diferentes criterios (estado, nombre o rol)",
            operationId = "searchEmployees"
    )
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(
            @Parameter(description = "Estado del empleado (ej. 'ACTIVE', 'INACTIVE')") @RequestParam(required = false) EmployeeStatus status,
            @Parameter(description = "Nombre completo o parcial del empleado") @RequestParam(required = false) String name,
            @Parameter(description = "Rol del empleado") @RequestParam(required = false) EmployeeRole role) {

        List<EmployeeDto> employees = employeeUseCase.searchEmployees(name, role, status).stream()
                .map(employeeApiMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }
}
