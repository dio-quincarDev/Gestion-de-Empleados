package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.services.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType; // Esta importación ya no es estrictamente necesaria si solo usas los String literales
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reportes", description = "API para la generación y consulta de reportes operativos.")
@RequiredArgsConstructor
public class ReportController {

    private final ReportingService reportingService;

    @Operation(summary = "Obtener reporte operativo completo",
            description = "Genera un reporte consolidado con información de consumos y asistencia. Permite filtrar por un rango de fechas y, opcionalmente, por un empleado específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado y recuperado exitosamente.",
                    content = @Content(mediaType = "application/json", // CAMBIO AQUÍ
                            schema = @Schema(implementation = ReportDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida. Las fechas de inicio y fin son obligatorias o tienen un formato incorrecto.",
                    content = @Content(mediaType = "text/plain")), // CAMBIO AQUÍ
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado con el ID proporcionado.",
                    content = @Content(mediaType = "text/plain")), // CAMBIO AQUÍ
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la solicitud del reporte.",
                    content = @Content(mediaType = "text/plain")) // CAMBIO AQUÍ
    })
    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteReport(
            @Parameter(description = "Fecha de inicio del periodo del reporte en formato罄-MM-DD. **Obligatorio.**", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha de fin del periodo del reporte en formato罄-MM-DD. **Obligatorio.**", required = true, example = "2023-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "ID único del empleado para filtrar el reporte. Si se omite, el reporte incluirá datos de todos los empleados.", example = "1")
            @RequestParam(required = false) Long employeeId) {

        if (startDate == null || endDate == null) {
            return ResponseEntity.badRequest().body("Las fechas son obligatorias.");
        }

        try {
            ReportDto report = reportingService.generateCompleteReport(startDate, endDate, employeeId);
            return ResponseEntity.ok(report);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte.");
        }
    }
}