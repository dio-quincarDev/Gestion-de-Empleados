package com.employed.bar.domain.port.in.service;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

import java.util.List;

public interface SendEmployeeReportNotificationUseCase {
    void sendReport(List<EmployeeClass> employees, List<Report> reports);
}
