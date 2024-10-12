package com.employed.bar.ports.in;

import com.employed.bar.adapters.dtos.ReportDto;

import java.time.LocalDateTime;

public interface ReportingPort {
    ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId);

    void sendWeeklyReports(String email, String subject, String body);
}

