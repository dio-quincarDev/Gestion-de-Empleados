package com.employed.bar.infrastructure.adapter.in.controller.kpi;

import com.employed.bar.domain.model.kpi.ManagerKpis;
import com.employed.bar.domain.port.in.service.KpiServicePort;
import com.employed.bar.infrastructure.constants.ApiPathConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for retrieving Key Performance Indicators (KPIs).
 * This controller provides endpoints for accessing various KPI metrics, primarily for management roles.
 * It acts as an inbound adapter to the application's KPI service functionalities.
 */
@RestController
@RequestMapping(ApiPathConstants.V1_ROUTE + ApiPathConstants.KPI_ROUTE)
@Tag(name = "KPIs", description = "Endpoints for retrieving Key Performance Indicators.")
@RequiredArgsConstructor
public class KpiController {

    private final KpiServicePort kpiServicePort;

    @Operation(summary = "Get Manager KPIs",
            description = "Retrieves a set of Key Performance Indicators for the manager for a given date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "KPIs retrieved successfully.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/manager")
    public ResponseEntity<ManagerKpis> getManagerKpis(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        ManagerKpis kpis = kpiServicePort.getManagerKpis(startDate, endDate);
        return ResponseEntity.ok(kpis);
    }
}
