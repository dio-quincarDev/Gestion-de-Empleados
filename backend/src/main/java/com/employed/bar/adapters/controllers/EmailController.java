package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.adapters.integrations.EmailService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.EmployeeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/email")
@Tag(name = "Email", description = "Envio de Email")
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



    @GetMapping("/send-test")
    public ResponseEntity <String> sendTestEmail(@RequestParam Long employeeId) {
        LocalDate date = LocalDate.parse("2024-10-10");
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("No se encontro Empleado con ID" + employeeId));

        ReportDto report = reportingService.generateCompleteReport(date, date, employeeId);
            System.out.println("Report for employee " + employee.getName() + " : " + report.toString());

        String emailBody = emailService.generateEmailBody(report, employee);
            System.out.println("Email Body: " + emailBody);

        emailService.sendHtmlMessage(employee.getEmail(), "Correro de Prueba", emailBody);
        return ResponseEntity.ok("Correo de Prueba enviado a: " + employee.getEmail());
    }

    @GetMapping("/send-report")
    public ResponseEntity <String> sendWeeklyReports(@RequestParam LocalDate date, Long employeeId, @RequestParam String startDate, @RequestParam String endDate){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("No se encontro Empleado con ID" + employeeId));

        LocalDate startDateParsed = LocalDate.parse(startDate);
        LocalDate endDateParsed = LocalDate.parse(endDate);
        ReportDto report = reportingService.generateCompleteReport(startDateParsed,endDateParsed, employeeId);
        String emailBody = emailService.generateEmailBody(report, employee);
        emailService.sendHtmlMessage(employee.getEmail(), "Weekly Report", emailBody);


        return ResponseEntity.ok("Weekly report sent to: " + employee.getEmail());
    }

    @PostMapping("/send-bulk-emails")
    public ResponseEntity<String> sendBulkEmails() {
        List<Employee> employees = employeeRepository.findAll();
        List<ReportDto> reports = employees.stream()
                .map(employee -> reportingService.generateCompleteReport(LocalDate.now(),LocalDate.now(), employee.getId()))
                .collect(Collectors.toList());

        reportingService.sendBulkEmails(employees, reports);
        return ResponseEntity.ok("Bulk emails sent successfully");
    }


    @PostMapping("/send-test-bulk-emails")
    public ResponseEntity<String> sendTestBulkEmails() {
        reportingService.sendTestBulkEmails();
        return ResponseEntity.ok("Test bulk emails sent successfully");
    }



}
