package com.employed.bar.application.service.manager;

import com.employed.bar.domain.event.ManagerReportGeneratedEvent;
import com.employed.bar.infrastructure.adapter.out.notification.EmailAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerNotificationApplicationService {

    private final EmailAdapter emailAdapter;

    @EventListener
    public void handleManagerReportGenerated(ManagerReportGeneratedEvent event) {
        // TODO: Make the manager's email configurable
        String managerEmail = "manager@example.com";
        emailAdapter.sendManagerReportByEmail(managerEmail, event.getManagerReport());
    }
}
