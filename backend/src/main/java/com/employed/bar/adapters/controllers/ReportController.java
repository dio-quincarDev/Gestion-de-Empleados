package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.services.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "API para generar y enviar reportes")
public class ReportController {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }


    @GetMapping("/complete")
    @Operation(summary = "Obtener reporte completo", description = "Genera un reporte completo para una fecha específica y opcionalmente para un empleado específico")
    @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente")
    @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    public ResponseEntity<ReportDto> getCompleteReport(

            @Parameter(description = "Fecha Inicial del reporte", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Fecha Final del reporte", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,


            @Parameter(description = "ID del empleado (opcional)")
            @RequestParam(required = false) Long employeeId) {

        if (startDate == null || endDate == null) {
            return ResponseEntity.badRequest().body(null);
        } try {
             ReportDto report = reportingService.generateCompleteReport(startDate, endDate, employeeId);
            return ResponseEntity.ok(report);
        }catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }


    }

}