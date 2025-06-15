package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.adapters.integrations.EmailService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/email")
@Tag(name = "Gestión de Emails", description = "API para el envío y la gestión de correos electrónicos, incluyendo reportes.") // Ajuste del nombre y descripción del Tag
public class EmailController {
    // Implementación del método para enviar correos electrónicos

    private final EmailService emailService;
    private final ReportingService reportingService;
    private final EmployeeRepository employeeRepository;

    public EmailController(EmailService emailService, ReportingService reportingService, EmployeeRepository employeeRepository) {
        this.emailService = emailService;
        this.reportingService = reportingService;
        this.employeeRepository = employeeRepository;
    }


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
    public ResponseEntity <String> sendTestEmail(
            @Parameter(description = "ID único del empleado al que se enviará el correo de prueba.", required = true, example = "1")
            @RequestParam Long employeeId) {
        LocalDate date = LocalDate.parse("2024-10-10"); // NOTA: Esta fecha es fija en el código.
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("No se encontro Empleado con ID" + employeeId));

        ReportDto report = reportingService.generateCompleteReport(date, date, employeeId);
        System.out.println("Report for employee " + employee.getName() + " : " + report.toString());

        String emailBody = emailService.generateEmailBody(report, employee);
        System.out.println("Email Body: " + emailBody);

        emailService.sendHtmlMessage(employee.getEmail(), "Correo de Prueba", emailBody);
        return ResponseEntity.ok("Correo de Prueba enviado a: " + employee.getEmail());
    }

    @Operation(summary = "Enviar reporte semanal a un empleado",
            description = "Envía un reporte semanal personalizado a un empleado específico, basado en un rango de fechas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte semanal enviado exitosamente.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Fechas de inicio o fin inválidas.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado con el ID proporcionado.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al enviar el reporte semanal.",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/send-report")
    public ResponseEntity <String> sendWeeklyReports(
            @Parameter(description = "Fecha actual (usada para contexto, no para rango de reporte). Formato YYYY-MM-DD.", example = "2024-06-15")
            @RequestParam LocalDate date, // Nota: El uso de este parámetro 'date' no parece afectar directamente el rango del reporte según la lógica actual.

            @Parameter(description = "ID único del empleado al que se enviará el reporte.", required = true, example = "1")
            @RequestParam Long employeeId,

            @Parameter(description = "Fecha de inicio del periodo del reporte semanal. Formato YYYY-MM-DD. **Obligatorio.**", required = true, example = "2024-06-01")
            @RequestParam String startDate,

            @Parameter(description = "Fecha de fin del periodo del reporte semanal. Formato YYYY-MM-DD. **Obligatorio.**", required = true, example = "2024-06-07")
            @RequestParam String endDate) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("No se encontro Empleado con ID" + employeeId));

        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);
        ReportDto report = reportingService.generateCompleteReport(startDateParsed,endDateParsed, employeeId);
        String emailBody = emailService.generateEmailBody(report, employee);
        emailService.sendHtmlMessage(employee.getEmail(), "Weekly Report", emailBody);

        return ResponseEntity.ok("Weekly report sent to: " + employee.getEmail());
    }

    @Operation(summary = "Enviar correos masivos a todos los empleados en lotes",
            description = "Envía reportes individuales a todos los empleados registrados, procesando el envío en lotes para optimizar el rendimiento y evitar sobrecargar el servidor de correo. Los reportes cubren la última semana.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correos masivos enviados exitosamente en lotes.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "No se encontraron empleados para enviar correos masivos.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor durante el envío de correos masivos.",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/send-bulk-emails")
    public ResponseEntity<String> sendBulkEmails(
            @Parameter(description = "Tamaño del lote de empleados a procesar en cada envío asíncrono.", required = true, example = "10")
            @RequestParam int batchSize) {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            return ResponseEntity.badRequest().body("No employees found.");
        }

        List<CompletableFuture<Void>> batchFutures = new ArrayList<>();
        for (int i = 0; i < employees.size(); i += batchSize) {
            List<Employee> batchEmployees = employees.subList(i, Math.min(i + batchSize, employees.size()));
            batchFutures.add(sendEmailsInBatch(batchEmployees));
        }

        CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();
        return ResponseEntity.ok("Bulk emails sent successfully in batches.");
    }

    private CompletableFuture<Void> sendEmailsInBatch(List<Employee> employees) {
        List<CompletableFuture<Void>> futures = employees.stream()
                .map(employee -> {
                    ReportDto report = reportingService.generateCompleteReport(
                            LocalDate.now().minusWeeks(1), LocalDate.now(), employee.getId());
                    return emailService.sendEmailAsync(employee, report);
                })
                .collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }


    @Operation(summary = "Enviar correos masivos de prueba a un subconjunto de empleados",
            description = "Envía correos de prueba a un número limitado de empleados (por defecto 5) para verificar la funcionalidad de envío masivo sin afectar a todos los empleados. Los reportes utilizan una fecha fija.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correos masivos de prueba enviados exitosamente.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "No se encontraron empleados para la prueba de envío masivo.",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor durante el envío de correos masivos de prueba.",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/send-test-bulk-emails")
    public ResponseEntity<String> sendTestBulkEmails(
            @Parameter(description = "Número de empleados a los que se enviará correos de prueba. Por defecto es 5.", example = "3")
            @RequestParam(defaultValue = "5") int testBatchSize) {
        List<Employee> employees = employeeRepository.findAll()
                .stream()
                .limit(testBatchSize)
                .collect(Collectors.toList());

        if (employees.isEmpty()){
            return ResponseEntity.badRequest().body("Not Employee for test bulk emails");
        }
        List<CompletableFuture<Void>> emailFutures = employees.stream()
                .map(employee -> {
                    LocalDate date = LocalDate.parse("2024-10-10"); // NOTA: Esta fecha es fija en el código de prueba.
                    ReportDto report = reportingService.generateCompleteReport(date, date, employee.getId());
                    return emailService.sendEmailAsync(employee, report);
                })
                .collect(Collectors.toList());
        CompletableFuture.allOf(emailFutures.toArray(new CompletableFuture[0])).join();
        return ResponseEntity.ok("Test bulk emails sent successfully.");
    }
}