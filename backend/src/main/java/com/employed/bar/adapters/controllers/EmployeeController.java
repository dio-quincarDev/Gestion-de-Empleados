package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.EmployeeDto;
import com.employed.bar.application.EmployeeApplicationService;
import com.employed.bar.domain.exceptions.EmailAlreadyExistException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "1. Gestión de Empleados", description = "Endpoints para la administración del personal")
public class EmployeeController {
    private final EmployeeApplicationService employeeApplicationService;

    public EmployeeController(EmployeeApplicationService employeeApplicationService) {
        this.employeeApplicationService = employeeApplicationService;
    }

    @Operation(
            summary = "Crear nuevo empleado",
            description = "Registra un nuevo empleado en el sistema",
            operationId = "createEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid input data\"}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El email ya está registrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Email already exists\"}"))
            )
    })
    @PostMapping("/")
    public ResponseEntity<Employee> createEmployee(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del empleado a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))
            )
            @RequestBody @Valid EmployeeDto employeeDto) {
        try {
            Employee createdEmployee = employeeApplicationService.createEmployee(employeeDto);
            return ResponseEntity.ok(createdEmployee);
        } catch (EmailAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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
                    content = @Content(schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(
            @Parameter(description = "ID del empleado", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Employee employee = employeeApplicationService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "Listar todos los empleados",
            description = "Obtiene una lista completa de todos los empleados registrados",
            operationId = "getAllEmployees"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de empleados obtenida exitosamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Employee.class)))
    )
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeApplicationService.getEmployees();
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
                    content = @Content(schema = @Schema(implementation = Employee.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El nuevo email ya está registrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Email already exists\"}"))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(
            @Parameter(description = "ID del empleado a actualizar", required = true, example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del empleado",
                    required = true,
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class)))
            @RequestBody @Valid EmployeeDto employeeDto) {
        try {
            Employee updatedEmployee = employeeApplicationService.updateEmployee(id, employeeDto);
            return ResponseEntity.ok(updatedEmployee);
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID del empleado a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        try {
            employeeApplicationService.deleteEmployee(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
            summary = "Buscar empleados",
            description = "Busca empleados por diferentes criterios (estado, nombre o rol)",
            operationId = "searchEmployees"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resultados de búsqueda",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Employee.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "No se proporcionaron parámetros de búsqueda",
                    content = @Content(schema = @Schema(example = "{\"message\": \"No search parameters provided\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontraron resultados",
                    content = @Content(schema = @Schema(example = "{\"message\": \"No employees found\"}"))
            )
    })
    @GetMapping("/search")
    public ResponseEntity<?> getEmployeeByStatus(
            @Parameter(description = "Estado del empleado (ej. 'ACTIVE', 'INACTIVE')", example = "ACTIVE")
            @RequestParam(required = false) String status,

            @Parameter(description = "Nombre completo o parcial del empleado", example = "Juan Pérez")
            @RequestParam(required = false) String name,

            @Parameter(description = "Rol del empleado (ej. 'MESERO', 'COCINERO')", example = "MESERO")
            @RequestParam(required = false) String role) {

        if (status != null) {
            List<Employee> employees = employeeApplicationService.getEmployeeByStatus(status);
            return ResponseEntity.ok(employees);
        } else if (name != null) {
            Optional<Employee> employee = employeeApplicationService.getEmployeeByName(name);
            return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else if (role != null) {
            Optional<Employee> employee = employeeApplicationService.getEmployeeByRole(role);
            return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}