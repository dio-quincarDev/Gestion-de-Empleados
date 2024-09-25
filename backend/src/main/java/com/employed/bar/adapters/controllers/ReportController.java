package com.employed.bar.adapters.controllers;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ReportingPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportingService reportingService;

    public ReportController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }


    @GetMapping("/complete")
    public ResponseEntity<ReportDto> getCompleteReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam( required = false) Long employeeId) {
        if (date == null ) {
            return ResponseEntity.badRequest().body(null);
        }

        ReportDto report = reportingService.generateCompleteReport(date, employeeId);
        return ResponseEntity.ok(report);
    }
}
