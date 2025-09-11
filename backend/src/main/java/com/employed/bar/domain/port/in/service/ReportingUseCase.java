package com.employed.bar.domain.port.in.service;


import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.time.LocalDate;
import java.util.List;


public interface ReportingUseCase {
    Report generateCompleteReport(LocalDate startDate, LocalDate endDate, Long employeeId);
    void sendWeeklyReports();
    void sendTestEmail();

    void sendTestEmailToEmployee(Long employeeId);

    void sendBulkEmails(List<EmployeeClass> employees, List<Report> reports );

    void sendTestBulkEmails();
}

