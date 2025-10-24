package com.employed.bar.application.notification;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.in.service.SendEmployeeReportNotificationUseCase;
import com.employed.bar.domain.port.out.NotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService implements SendEmployeeReportNotificationUseCase {

    private final NotificationPort notificationPort;

    @Override
    public void sendReport(List<EmployeeClass> employees, List<Report> reports) {
        System.out.println("📨 [NOTIFICATION SERVICE] Enviando reportes para " + employees.size() + " empleados");
        for (int i = 0; i < employees.size(); i++) {
            System.out.println("   - Enviando email a: " + employees.get(i).getEmail());
            notificationPort.sendReportByEmail(employees.get(i), reports.get(i));
        }
    }
}
