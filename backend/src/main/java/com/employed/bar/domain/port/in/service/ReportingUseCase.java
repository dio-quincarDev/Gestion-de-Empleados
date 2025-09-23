package com.employed.bar.domain.port.in.service;


import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;

import java.time.LocalDate;

public interface ReportingUseCase {
    Report generateCompleteReportForEmployeeById(LocalDate startDate, LocalDate endDate, Long employeeId);
    void sendTestEmailToEmployee(Long employeeId);
    void generateAndSendWeeklyReport(LocalDate startDate, LocalDate endDate);
}

