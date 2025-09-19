package com.employed.bar.infrastructure.adapter.in.controller.kpi;

import com.employed.bar.domain.model.kpi.ManagerKpis;
import com.employed.bar.domain.port.in.service.KpiServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/kpis")
@Tag(name = "KPIs", description = "Endpoints for retrieving Key Performance Indicators.")
@RequiredArgsConstructor
public class KpiController {

    private final KpiServicePort kpiServicePort;

    @Operation(summary = "Get Manager KPIs",
            description = "Retrieves a set of Key Performance Indicators for the manager for a given date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KPIs retrieved successfully.")
    })
    @GetMapping("/manager")
    public ResponseEntity<ManagerKpis> getManagerKpis(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        ManagerKpis kpis = kpiServicePort.getManagerKpis(startDate, endDate);
        return ResponseEntity.ok(kpis);
    }
}
