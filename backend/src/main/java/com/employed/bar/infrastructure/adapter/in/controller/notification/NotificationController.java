package com.employed.bar.infrastructure.adapter.in.controller.notification;

import com.employed.bar.domain.event.TestEmailRequestedEvent;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.port.out.EmployeeRepositoryPort;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing notifications within the application.
 * This controller provides endpoints for triggering notification events, such as sending test emails.
 * It acts as an inbound adapter to the application's notification functionalities.
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.NOTIFICATION_ROUTE)
@Tag(name = "Gestión de Notificaciones", description = "API para el envío y la gestión de notificaciones, incluyendo correos electrónicos.")
@RequiredArgsConstructor
public class NotificationController {

    private final ApplicationEventPublisher eventPublisher;
    private final EmployeeRepositoryPort employeeRepositoryPort;

    @Operation(summary = "Enviar correo de prueba a un empleado",
            description = "Solicita el envío de un correo electrónico de prueba con un reporte básico a un empleado específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud de correo de prueba procesada.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado.")
    })
    @GetMapping("/send-test")
    public ResponseEntity<String> sendTestEmail(
            @Parameter(description = "ID único del empleado al que se enviará el correo de prueba.", required = true, example = "1")
            @RequestParam Long employeeId) {

        // Validar que el employee existe ANTES de publicar el evento
        try {
            // Buscar el employee (esto lanzará EmployeeNotFoundException si no existe)
            employeeRepositoryPort.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

            eventPublisher.publishEvent(new TestEmailRequestedEvent(this, employeeId));
            return ResponseEntity.ok("Solicitud de envío de correo de prueba para el empleado " + employeeId + " ha sido procesada.");
        } catch (EmployeeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
        }
    }
}