package com.employed.bar.domain.port.out;

import com.employed.bar.infrastructure.dto.ReportDto;
import com.employed.bar.domain.model.EmployeeClass;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingPort {
    ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId);

    void sendWeeklyReports(String email, String subject, String body);

    void sendTestEmail(String email, String subject, String body);

    void sendBulkEmails(List<EmployeeClass> employees, List<ReportDto> reports);

}

