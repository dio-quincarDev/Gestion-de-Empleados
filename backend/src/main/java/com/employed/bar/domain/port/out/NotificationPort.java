package com.employed.bar.domain.port.out;

import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;

public interface NotificationPort {
    void sendReportByEmail(EmployeeClass employee, Report report);
    void sendManagerReportByEmail(String managerEmail, ManagerReport managerReport);
}