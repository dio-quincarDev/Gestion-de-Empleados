package com.employed.bar.infrastructure.adapter.in.controller.manager;

import com.employed.bar.domain.event.ManagerReportRequestedEvent;
import com.employed.bar.domain.port.in.service.GenerateManagerReportPdfUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/manager/reports")
@Tag(name = "Manager Reports", description = "Endpoints for generating manager reports.")
@RequiredArgsConstructor
public class ManagerReportController {

    private final ApplicationEventPublisher eventPublisher;
    private final GenerateManagerReportPdfUseCase generateManagerReportPdfUseCase;

    @Operation(summary = "Generate Manager Weekly Report",
            description = "Triggers the generation of the manager's weekly report for a given date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Manager report generation triggered successfully.")
    })
    @PostMapping("/weekly")
    public ResponseEntity<String> generateManagerWeeklyReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        eventPublisher.publishEvent(new ManagerReportRequestedEvent(this, startDate, endDate));
        return ResponseEntity.ok("Manager report generation triggered successfully.");
    }

    @Operation(summary = "Download Manager Weekly Report as PDF",
            description = "Downloads the manager's weekly report as a PDF for a given date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF report generated and downloaded successfully.",
                    content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "500", description = "Error generating PDF report.")
    })
    @GetMapping("/weekly/pdf")
    public ResponseEntity<byte[]> downloadManagerWeeklyReportPdf(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        byte[] pdfBytes = generateManagerReportPdfUseCase.generateManagerReportPdf(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "manager_weekly_report.pdf");
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }
}
