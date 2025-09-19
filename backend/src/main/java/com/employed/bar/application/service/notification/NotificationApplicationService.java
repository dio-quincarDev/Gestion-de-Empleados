package com.employed.bar.application.service.notification;

import com.employed.bar.domain.event.ReportGeneratedEvent;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService {

    private final NotificationPort notificationPort;

    @EventListener
    public void handleReportGeneratedEvent(ReportGeneratedEvent event) {
        List<EmployeeClass> employees = event.getEmployees();
        List<Report> reports = event.getReports();

        for (int i = 0; i < employees.size(); i++) {
            notificationPort.sendReportByEmail(employees.get(i), reports.get(i));
        }
    }
}
