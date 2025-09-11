package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportingPort {
    Report generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId);

    void sendReportByEmail(EmployeeClass employee, Report report);

    void sendWeeklyReports(String email, String subject, String body);

    void sendTestEmail(String email, String subject, String body);

    void sendBulkEmails(List<EmployeeClass> employees, List<Report> reports);

}

