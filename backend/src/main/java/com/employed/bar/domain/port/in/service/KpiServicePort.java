package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.kpi.ManagerKpis;

import java.time.LocalDate;

public interface KpiServicePort {
    ManagerKpis getManagerKpis(LocalDate startDate, LocalDate endDate);
}
