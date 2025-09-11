package com.employed.bar.domain.port.in.service;


import com.employed.bar.infrastructure.dto.report.ReportDto;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.time.LocalDate;
import java.util.List;


public interface ReportingUseCase {
    ReportDto generateCompleteReport(LocalDate startDate,LocalDate endDate, Long employeeId);
    void sendWeeklyReports();
    void sendTestEmail();
    void sendBulkEmails(List<EmployeeClass> employees, List<ReportDto> reports );

    void sendTestBulkEmails();
}

