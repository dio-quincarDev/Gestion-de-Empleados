package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;

public interface NotificationPort {
    void sendReportByEmail(EmployeeClass employee, Report report);
}