package com.employed.bar.infrastructure.adapter.in.controller.app;

import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.ConsumptionDto;
import com.employed.bar.application.service.ConsumptionApplicationService;
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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.CONSUMPTION_ROUTE)
@Tag(name = "4. Gestión de Consumos", description = "Endpoints para el registro y consulta de consumos del personal")
public class ConsumptionController {
    private final ConsumptionApplicationService consumptionApplicationService;
    private final ConsumptionApiMapper consumptionApiMapper;

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
    public ResponseEntity<BigDecimal> getTotalConsumptionForAllEmployees(
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true, example = "2023-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        BigDecimal total = consumptionApplicationService.calculateTotalConsumptionForAllEmployees(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return ResponseEntity.ok(total);
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
}