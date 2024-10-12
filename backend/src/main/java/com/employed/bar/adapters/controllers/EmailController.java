package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.adapters.integrations.EmailService;
import com.employed.bar.domain.exceptions.EmployeeNotFoundException;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.EmployeeRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity <String> sendTestEmail(@RequestParam Long employeeId){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("No se encontro Empleado con ID" + employeeId));
                    List<AttendanceReportDto> attendanceReports = List.of();
                    List<ConsumptionReportDto> consumptionReports = List.of();
        ReportDto report = new ReportDto(attendanceReports, consumptionReports);

        String emailBody = emailService.generateEmailBody(report, employee);
        emailService.sendHtmlMessage(employee.getEmail(), "Test Email", emailBody);

        return ResponseEntity.ok("Email enviado con éxito " + employee.getEmail());

    }

    @GetMapping("/send-report")
    public ResponseEntity <String> sendWeeklyReports(@RequestParam LocalDate date, Long employeeId, @RequestParam String startDate, @RequestParam String endDate){
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("No se encontro Empleado con ID" + employeeId));

        ReportDto report = reportingService.generateCompleteReport(date, employeeId);
        String emailBody = emailService.generateEmailBody(report, employee);
        emailService.sendHtmlMessage(employee.getEmail(), "Weekly Report", emailBody);


        return ResponseEntity.ok("Weekly report sent to: " + employee.getEmail());
    }

}
