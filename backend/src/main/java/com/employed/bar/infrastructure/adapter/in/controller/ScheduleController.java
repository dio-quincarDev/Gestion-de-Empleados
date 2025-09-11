package com.employed.bar.infrastructure.adapter.in.controller;

import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.model.strucuture.ScheduleClass;
import com.employed.bar.domain.port.in.service.ScheduleUseCase;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.adapter.in.mapper.ScheduleApiMapper;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.domain.ScheduleDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.SCHEDULE_ROUTE)
@Tag(name = "2. Gestión de Horarios", description = "Endpoints para la administración de horarios del personal")
public class ScheduleController {
    private final ScheduleUseCase scheduleUseCase;
    private final ScheduleApiMapper scheduleApiMapper;
    private final EmployeeRepositoryPort employeeRepositoryPort;

    public ScheduleController(ScheduleUseCase scheduleUseCase, ScheduleApiMapper scheduleApiMapper, EmployeeRepositoryPort employeeRepositoryPort) {
        this.scheduleUseCase = scheduleUseCase;
        this.scheduleApiMapper = scheduleApiMapper;
        this.employeeRepositoryPort = employeeRepositoryPort;
    }

    @Operation(
            summary = "Crear nuevo horario",
            description = "Registra un nuevo turno/horario para un empleado",
            operationId = "createSchedule"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Horario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ScheduleDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid schedule data\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @PostMapping("/")
    public ResponseEntity<ScheduleDto> createSchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del horario a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleDto.class)))
            @RequestBody ScheduleDto scheduleDto) {

        ScheduleClass schedule = scheduleApiMapper.toDomain(scheduleDto);
        EmployeeClass employee = employeeRepositoryPort.findById(scheduleDto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + scheduleDto.getEmployeeId()));
        schedule.setEmployee(employee);

        ScheduleClass createdSchedule = scheduleUseCase.createSchedule(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleApiMapper.toDto(createdSchedule));
    }

    @Operation(
            summary = "Obtener horario por ID",
            description = "Recupera un horario específico por su identificador único",
            operationId = "getScheduleById"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Horario encontrado",
                    content = @Content(schema = @Schema(implementation = ScheduleDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Schedule not found\"}"))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDto> getSchedule(
            @Parameter(description = "ID del horario", required = true, example = "1")
            @PathVariable Long id) {

        ScheduleClass schedule = scheduleUseCase.getScheduleById(id);
        return ResponseEntity.ok(scheduleApiMapper.toDto(schedule));
    }

    @Operation(
            summary = "Listar horarios por empleado",
            description = "Obtiene todos los horarios asignados a un empleado específico",
            operationId = "getSchedulesByEmployee"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de horarios obtenida exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ScheduleDto.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ScheduleDto>> getSchedules(
            @Parameter(description = "ID del empleado", required = true, example = "1")
            @PathVariable Long employeeId) {

        List<ScheduleClass> schedules = scheduleUseCase.getSchedulesByEmployee(employeeId);
        return ResponseEntity.ok(schedules.stream().map(scheduleApiMapper::toDto).collect(Collectors.toList()));
    }

    @Operation(
            summary = "Actualizar horario",
            description = "Modifica los datos de un horario existente",
            operationId = "updateSchedule"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Horario actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = ScheduleDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid schedule data\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Schedule not found\"}"))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDto> updateSchedule(
            @Parameter(description = "ID del horario a actualizar", required = true, example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del horario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleDto.class)))
            @RequestBody ScheduleDto scheduleDto) {

        ScheduleClass schedule = scheduleApiMapper.toDomain(scheduleDto);
        ScheduleClass updatedSchedule = scheduleUseCase.updateSchedule(id, schedule);
        return ResponseEntity.ok(scheduleApiMapper.toDto(updatedSchedule));
    }

    @Operation(
            summary = "Eliminar horario",
            description = "Remueve un horario del sistema",
            operationId = "deleteSchedule"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Horario eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Schedule not found\"}"))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @Parameter(description = "ID del horario a eliminar", required = true, example = "1")
            @PathVariable Long id) {

        scheduleUseCase.deleteSchedule(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}