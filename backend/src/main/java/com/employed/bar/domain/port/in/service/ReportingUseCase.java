package com.employed.bar.domain.port.in.service;


import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.time.LocalDate;

public interface ReportingUseCase {
    Report generateCompleteReport(LocalDate startDate, LocalDate endDate, Long employeeId);
    Report generateCompleteReport(LocalDate startDate, LocalDate endDate, EmployeeClass employee);
    void sendTestEmailToEmployee(Long employeeId);
}

