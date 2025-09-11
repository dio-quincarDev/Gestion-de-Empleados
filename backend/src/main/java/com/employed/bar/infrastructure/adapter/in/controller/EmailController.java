package com.employed.bar.infrastructure.adapter.in.controller;

import com.employed.bar.domain.port.in.service.ReportingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/email")
@Tag(name = "Gestión de Emails", description = "API para el envío y la gestión de correos electrónicos, incluyendo reportes.")
@RequiredArgsConstructor
public class EmailController {

    private final ReportingUseCase reportingUseCase;

    @Operation(summary = "Enviar correo de prueba a un empleado",
            description = "Envía un correo electrónico de prueba con un reporte básico a un empleado específico. Utiliza una fecha fija para el reporte.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo de prueba enviado exitosamente.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado con el ID proporcionado.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al enviar el correo de prueba.",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/send-test")
    public ResponseEntity<String> sendTestEmail(
            @Parameter(description = "ID único del empleado al que se enviará el correo de prueba.", required = true, example = "1")
            @RequestParam Long employeeId) {
        
        reportingUseCase.sendTestEmailToEmployee(employeeId);
        return ResponseEntity.ok("Solicitud de envío de correo de prueba para el empleado " + employeeId + " ha sido procesada.");
    }

    // The other endpoints (send-report, send-bulk-emails, etc.) should be refactored following the same pattern.
    // They are being left here for now to avoid breaking the API completely, but their logic should be moved to the application service.

}
