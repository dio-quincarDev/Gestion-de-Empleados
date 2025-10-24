package com.employed.bar.infrastructure.adapter.in.controller.manager;

import com.employed.bar.domain.port.in.service.ManagerReportServicePort;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for generating and managing manager-specific reports.
 * This controller provides endpoints for triggering the generation of weekly reports
 * and downloading them in PDF format. It acts as an inbound adapter to the application's
 * manager reporting functionalities.
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.REPORT_ROUTE)
@Tag(name = "Manager Reports", description = "Endpoints for generating manager reports.")
@RequiredArgsConstructor
public class ManagerReportController {

    private final ManagerReportServicePort managerReportServicePort;

    @Operation(summary = "Generate Manager Weekly Report",
            description = "Triggers the generation of the manager's weekly report for a given date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manager report generation triggered successfully.")
    })
    @PostMapping("/weekly")
    public ResponseEntity<String> generateManagerWeeklyReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body("End date cannot be before start date.");
        }
        managerReportServicePort.generateAndSendManagerReport(startDate, endDate);
        return ResponseEntity.ok("Manager report generation triggered successfully.");
    }

    @GetMapping("/weekly/pdf")
    public ResponseEntity<byte[]> downloadManagerReportPdf() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        byte[] pdf = managerReportServicePort.generateManagerReportPdf(startDate, endDate);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "manager-report.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}