package com.employed.bar.infrastructure.adapter.in.controller.app;

import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.ConsumptionDto;
import com.employed.bar.application.service.ConsumptionApplicationService;
import com.employed.bar.domain.exceptions.ConsumptionNotFoundException;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.exceptions.InvalidConsumptionDataException;
import com.employed.bar.domain.model.structure.ConsumptionClass;
import com.employed.bar.infrastructure.adapter.in.mapper.ConsumptionApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing employee consumption records.
 * This controller handles HTTP requests related to consumption, acting as an inbound adapter
 * to the application's core consumption management functionalities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.CONSUMPTION_ROUTE)
@Tag(name = "4. Gestión de Consumos", description = "Endpoints para el registro y consulta de consumos del personal")
public class ConsumptionController {
    private final ConsumptionApplicationService consumptionApplicationService;
    private final ConsumptionApiMapper consumptionApiMapper;
    private final EmployeeRepositoryPort employeeRepository;

    @Operation(
            summary = "Registrar nuevo consumo",
            description = "Endpoint para registrar un consumo realizado por un empleado",
            operationId = "createConsumption"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consumo registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = ConsumptionClass.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de consumo inválidos",
                    content = @Content(schema = @Schema(example = "{\"amount\": \"El monto debe ser positivo\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ConsumptionClass> createConsumption(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del consumo a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConsumptionDto.class))
            )
            @RequestBody @Valid ConsumptionDto consumptionDto) {
        ConsumptionClass consumptionClass = consumptionApiMapper.toDomain(consumptionDto);
        ConsumptionClass createdConsumption = consumptionApplicationService.createConsumption(consumptionClass);
        return ResponseEntity.ok(createdConsumption);
    }

    @Operation(
            summary = "Obtener todos los consumos",
            description = "Obtiene una lista de todos los registros de consumo",
            operationId = "getAllConsumptions"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de consumos",
                    content = @Content(schema = @Schema(implementation = ConsumptionDto.class))
            )
    })
    @GetMapping("/")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ConsumptionDto>> getAllConsumptions() {
        List<ConsumptionClass> consumptions = consumptionApplicationService.getAllConsumptions();
        return ResponseEntity.ok(consumptionApiMapper.toDtoList(consumptions));
    }

    @Operation(
            summary = "Obtener consumo por ID",
            description = "Obtiene los detalles de un registro de consumo específico por su ID",
            operationId = "getConsumptionById"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Detalles del consumo",
                    content = @Content(schema = @Schema(implementation = ConsumptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consumo no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Consumption not found\"}"))
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ConsumptionDto> getConsumptionById(
            @Parameter(description = "ID del consumo", required = true, example = "1")
            @PathVariable Long id) {
        return consumptionApplicationService.getConsumptionById(id)
                .map(consumptionApiMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Obtener total de consumos por empleado",
            description = "Calcula el monto total de consumos para un empleado en un rango de fechas",
            operationId = "getTotalConsumptionByEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Total calculado exitosamente",
                    content = @Content(schema = @Schema(type = "number", format = "decimal", example = "125.50"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid date range\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/total")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BigDecimal> getTotalConsumptionByEmployee(
            @Parameter(description = "ID del empleado", required = true, example = "1")
            @RequestParam Long employeeId,

            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true, example = "2023-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal total = consumptionApplicationService.calculateTotalConsumptionByEmployee(employeeId, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    @Operation(
            summary = "Obtener total de consumos de todos los empleados",
            description = "Calcula el monto total de consumos para todos los empleados en un rango de fechas",
            operationId = "getTotalConsumptionForAllEmployees"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Total calculado exitosamente",
                    content = @Content(schema = @Schema(type = "number", format = "decimal", example = "1250.75"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid date range\"}"))
            )
    })
    @GetMapping("/total/all")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BigDecimal> getTotalConsumptionForAllEmployees(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true, example = "2023-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal total = consumptionApplicationService.calculateTotalConsumptionForAllEmployees(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return ResponseEntity.ok(total);
    }

    @Operation(
            summary = "Actualizar consumo",
            description = "Actualiza un registro de consumo existente",
            operationId = "updateConsumption"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consumo actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = ConsumptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de consumo inválidos",
                    content = @Content(schema = @Schema(example = "{\"amount\": \"El monto debe ser positivo\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consumo o empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Consumption not found\"}"))
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ConsumptionDto> updateConsumption(
            @Parameter(description = "ID del consumo a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del consumo a actualizar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConsumptionDto.class))
            )
            @RequestBody @Valid ConsumptionDto consumptionDto) {
        ConsumptionClass consumptionClass = consumptionApiMapper.toDomain(consumptionDto);
        consumptionClass.setId(id);
        ConsumptionClass updatedConsumption = consumptionApplicationService.updateConsumption(consumptionClass);
        return ResponseEntity.ok(consumptionApiMapper.toDto(updatedConsumption));
    }

    @Operation(
            summary = "Eliminar consumo",
            description = "Elimina un registro de consumo por su ID",
            operationId = "deleteConsumption"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Consumo eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Consumo no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Consumption not found\"}"))
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteConsumption(
            @Parameter(description = "ID del consumo a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        consumptionApplicationService.deleteConsumption(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Obtener consumos por empleado",
            description = "Obtiene la lista de consumos de un empleado en un rango de fechas, opcionalmente filtrado por descripción",
            operationId = "getConsumptionsByEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de consumos del empleado",
                    content = @Content(schema = @Schema(implementation = ConsumptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid date range\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ConsumptionDto>> getConsumptionsByEmployee(
            @Parameter(description = "ID del empleado", required = true, example = "1")
            @PathVariable Long employeeId,

            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true, example = "2023-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Descripción del consumo (opcional)", required = false, example = "Almuerzo")
            @RequestParam(required = false) String description) {

        // Convertir LocalDate a LocalDateTime
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Obtener el empleado
        EmployeeClass employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        List<ConsumptionClass> consumptions = consumptionApplicationService.getConsumptionByEmployee(employee, startDateTime, endDateTime, description);
        return ResponseEntity.ok(consumptionApiMapper.toDtoList(consumptions));
    }




    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationConsumption(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return errors;
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidConsumptionDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleInvalidConsumptionDataException(InvalidConsumptionDataException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ConsumptionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleConsumptionNotFoundException(ConsumptionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}