package com.employed.bar.ports.out;

import com.employed.bar.adapters.dtos.ReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingPort {
    ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate);
}

