package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.report.Report;
import java.time.LocalDate;

public interface ReportingUseCase {
    Report generateCompleteReport(LocalDate startDate, LocalDate endDate, Long employeeId);

    void sendTestEmailToEmployee(Long employeeId);
}

