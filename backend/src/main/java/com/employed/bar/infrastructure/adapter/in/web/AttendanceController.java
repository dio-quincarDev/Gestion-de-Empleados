package com.employed.bar.infrastructure.adapter.in.web;

import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dtos.AttendanceDto;
import com.employed.bar.infrastructure.dtos.AttendanceReportDto;
import com.employed.bar.application.service.AttendanceApplicationService;
import com.employed.bar.domain.model.AttendanceRecord;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.ports.out.EmployeeRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.ATTENDANCE_ROUTE)
@Tag(name = "3. Gestión de Asistencia", description = "Endpoints para el registro y consulta de asistencia del personal")
public class AttendanceController {
    private final AttendanceApplicationService attendanceApplicationService;
    private final EmployeeRepositoryPort employeeRepository;

    public AttendanceController(AttendanceApplicationService attendanceApplicationService,
                                EmployeeRepositoryPort employeeRepository) {
        this.attendanceApplicationService = attendanceApplicationService;
        this.employeeRepository = employeeRepository;
    }

    @Operation(
            summary = "Registrar nueva asistencia",
            description = "Endpoint para registrar la entrada/salida de un empleado",
            operationId = "registerAttendance"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Asistencia registrada correctamente",
                    content = @Content(schema = @Schema(implementation = AttendanceRecord.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o faltantes",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee ID is required\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Internal server error\"}"))
            )
    })
    @PostMapping("/")
    public ResponseEntity<?> registerAttendance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de asistencia a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AttendanceDto.class)
                    )
            )
            @RequestBody @Valid AttendanceDto attendanceDto) {
        try {
            if (attendanceDto.getEmployeeId() == null) {
                return ResponseEntity.badRequest().body("Employee ID is required");
            }
            AttendanceRecord attendanceRecord = attendanceApplicationService.registerAttendance(attendanceDto);
            return ResponseEntity.ok(attendanceRecord);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Generar reporte de asistencia diaria",
            description = "Obtiene un reporte detallado de asistencia para un empleado en una fecha específica",
            operationId = "generateAttendanceReport"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reporte generado exitosamente",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttendanceReportDto.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid date parameters\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/report")
    public ResponseEntity<List<AttendanceReportDto>> generateAttendanceReport(
            @Parameter(
                    description = "ID del empleado",
                    required = true,
                    example = "1"
            )
            @RequestParam Long employeeId,

            @Parameter(
                    description = "Año (formato YYYY)",
                    required = true,
                    example = "2023"
            )
            @RequestParam int year,

            @Parameter(
                    description = "Mes (1-12)",
                    required = true,
                    example = "12"
            )
            @RequestParam int month,

            @Parameter(
                    description = "Día del mes",
                    required = true,
                    example = "15"
            )
            @RequestParam int day) {

        List<AttendanceReportDto> report = attendanceApplicationService.generateAttendanceReport(year, month, day, employeeId);
        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Calcular porcentaje de asistencia",
            description = "Calcula el porcentaje de asistencia de un empleado para un día específico",
            operationId = "calculateAttendancePercentage"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Porcentaje calculado exitosamente",
                    content = @Content(schema = @Schema(type = "number", format = "double", example = "95.5"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/percentage")
    public ResponseEntity<Double> calculateAttendancePercentage(
            @Parameter(
                    description = "ID del empleado",
                    required = true,
                    example = "1"
            )
            @RequestParam Long employeeId,

            @Parameter(
                    description = "Año (formato YYYY)",
                    required = true,
                    example = "2023"
            )
            @RequestParam int year,

            @Parameter(
                    description = "Mes (1-12)",
                    required = true,
                    example = "12"
            )
            @RequestParam int month,

            @Parameter(
                    description = "Día del mes",
                    required = true,
                    example = "15"
            )
            @RequestParam int day) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee Not Found"));
        double percentage = attendanceApplicationService.calculateAttendancePercentage(employee, year, month, day);
        return ResponseEntity.ok(percentage);
    }

    @Operation(
            summary = "Obtener registros de asistencia por rango de fechas",
            description = "Recupera todos los registros de asistencia de un empleado dentro de un rango de fechas específico",
            operationId = "getAttendanceByDateRange"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de registros de asistencia",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttendanceRecord.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de fecha inválidos",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Invalid date range\"}"))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Employee not found\"}"))
            )
    })
    @GetMapping("/list")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceListByEmployeeAndDateRange(
            @Parameter(
                    description = "ID del empleado",
                    required = true,
                    example = "1"
            )
            @RequestParam Long employeeId,

            @Parameter(
                    description = "Fecha de inicio (YYYY-MM-DD)",
                    required = true,
                    example = "2023-01-01"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(
                    description = "Fecha de fin (YYYY-MM-DD)",
                    required = true,
                    example = "2023-12-31"
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        List<AttendanceRecord> attendanceRecords = attendanceApplicationService
                .getAttendanceListByEmployeeAndDateRange(employee, startDate, endDate);
        return ResponseEntity.ok(attendanceRecords);
    }
}