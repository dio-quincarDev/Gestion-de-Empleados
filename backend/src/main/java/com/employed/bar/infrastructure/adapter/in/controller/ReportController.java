package com.employed.bar.infrastructure.adapter.in.controller;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.infrastructure.adapter.in.mapper.ReportApiMapper;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import com.employed.bar.infrastructure.dto.report.ReportDto;
import com.employed.bar.domain.port.in.service.GeneratePaymentUseCase;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.REPORT_ROUTE)
@Tag(name = "Reportes", description = "API para la generación y consulta de reportes operativos.")
@RequiredArgsConstructor
public class ReportController {

    private final ReportingUseCase reportingUseCase;
    private final ReportApiMapper reportApiMapper;
    private final GeneratePaymentUseCase generatePaymentUseCase;

    @Operation(summary = "Obtener reporte operativo completo",
            description = "Genera un reporte consolidado con información de consumos y asistencia. Permite filtrar por un rango de fechas y, opcionalmente, por un empleado específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado y recuperado exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReportDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida. Las fechas de inicio y fin son obligatorias o tienen un formato incorrecto.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado con el ID proporcionado.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la solicitud del reporte.",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/complete")
    public ResponseEntity<?> getCompleteReport(
            @Parameter(description = "Fecha de inicio del periodo del reporte en formato YYYY-MM-DD. **Obligatorio.**", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha de fin del periodo del reporte en formato YYYY-MM-DD. **Obligatorio.**", required = true, example = "2023-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "ID único del empleado para filtrar el reporte. Si se omite, el reporte incluirá datos de todos los empleados.", example = "1")
            @RequestParam(required = false) Long employeeId) {

        if (startDate == null || endDate == null) {
            return ResponseEntity.badRequest().body("Las fechas son obligatorias.");
        }

        try {
            Report report = reportingUseCase.generateCompleteReport(startDate, endDate, employeeId);
            ReportDto reportDto = reportApiMapper.toDto(report);
            return ResponseEntity.ok(reportDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado.");
        } catch (Exception e) {
            // It's good practice to log the exception here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al generar el reporte: " + e.getMessage());
        }
    }

    @Operation(summary = "Generar pago de empleado",
            description = "Calcula el pago total de un empleado para un rango de fechas dado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago calculado exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BigDecimal.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida. Las fechas de inicio y fin son obligatorias o tienen un formato incorrecto.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado con el ID proporcionado.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al procesar la solicitud.",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/payment")
    public ResponseEntity<BigDecimal> generatePayment(
            @Parameter(description = "ID único del empleado.", required = true, example = "1")
            @RequestParam Long employeeId,
            @Parameter(description = "Fecha de inicio del periodo en formato YYYY-MM-DD. **Obligatorio.**", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin del periodo en formato YYYY-MM-DD. **Obligatorio.**", required = true, example = "2023-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate == null || endDate == null) {
            return ResponseEntity.badRequest().body(BigDecimal.ZERO);
        }

        try {
            BigDecimal totalPayment = generatePaymentUseCase.generatePayment(employeeId, startDate, endDate);
            return ResponseEntity.ok(totalPayment);
        } catch (RuntimeException e) {
            // This will be caught by GlobalExceptionHandler if it's a specific exception
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BigDecimal.ZERO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BigDecimal.ZERO);
        }
    }
}
