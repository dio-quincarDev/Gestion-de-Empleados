package com.employed.bar.domain.services;


import com.employed.bar.adapters.dtos.ReportDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ReportingService {
    ReportDto generateCompleteReport(LocalDate date, Long employeeId);
}

