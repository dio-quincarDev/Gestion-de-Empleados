package com.employed.bar.infrastructure.adapter.in.web;

import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dtos.ScheduleDto;
import com.employed.bar.application.service.ScheduleApplicationService;
import com.employed.bar.domain.model.Schedule;
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

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.SCHEDULE_ROUTE)
@Tag(name = "2. Gestión de Horarios", description = "Endpoints para la administración de horarios del personal")
public class ScheduleController {
    private final ScheduleApplicationService scheduleApplicationService;

    public ScheduleController(ScheduleApplicationService scheduleApplicationService) {
        this.scheduleApplicationService = scheduleApplicationService;
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
                    content = @Content(schema = @Schema(implementation = Schedule.class))
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
    public ResponseEntity<Schedule> createSchedule(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del horario a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ScheduleDto.class)))
            @RequestBody ScheduleDto scheduleDto) {

        Schedule createdSchedule = scheduleApplicationService.createSchedule(scheduleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
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
                    content = @Content(schema = @Schema(implementation = Schedule.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Horario no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Schedule not found\"}"))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getSchedule(
            @Parameter(description = "ID del horario", required = true, example = "1")
            @PathVariable Long id) {

        Schedule schedule = scheduleApplicationService.getScheduleById(id);
        return ResponseEntity.ok(schedule);
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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Schedule.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Schedule>> getSchedules(
            @Parameter(description = "ID del empleado", required = true, example = "1")
            @PathVariable Long employeeId) {

        List<Schedule> schedules = scheduleApplicationService.getSchedulesByEmployee(employeeId);
        return ResponseEntity.ok(schedules);
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
                    content = @Content(schema = @Schema(implementation = Schedule.class))
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
    public ResponseEntity<Schedule> updateSchedule(
            @Parameter(description = "ID del horario a actualizar", required = true, example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del horario",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Schedule.class)))
            @RequestBody Schedule schedule) {

        Schedule updatedSchedule = scheduleApplicationService.updateSchedule(id, schedule);
        return ResponseEntity.ok(updatedSchedule);
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

        scheduleApplicationService.deleteSchedule(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}