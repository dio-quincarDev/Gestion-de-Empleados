package com.employed.bar.domain.ports.out;

import com.employed.bar.infrastructure.dtos.ReportDto;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingPort {
    ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId);

    void sendWeeklyReports(String email, String subject, String body);

    void sendTestEmail(String email, String subject, String body);

    void sendBulkEmails(List<Employee> employees, List<ReportDto> reports);

}

