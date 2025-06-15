package com.employed.bar.domain.services;


import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.Employee;

import java.time.LocalDate;
import java.util.List;


public interface ReportingService {
    ReportDto generateCompleteReport(LocalDate startDate,LocalDate endDate, Long employeeId);
    void sendWeeklyReports();
    void sendTestEmail();
    void sendBulkEmails(List<Employee> employees, List<ReportDto> reports );

    void sendTestBulkEmails();
}

