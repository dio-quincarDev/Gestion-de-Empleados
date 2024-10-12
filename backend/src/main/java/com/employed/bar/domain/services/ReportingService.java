package com.employed.bar.domain.services;


import com.employed.bar.adapters.dtos.ReportDto;

import java.time.LocalDate;


public interface ReportingService {
    ReportDto generateCompleteReport(LocalDate date, Long employeeId);
    void sendWeeklyReports();
}

