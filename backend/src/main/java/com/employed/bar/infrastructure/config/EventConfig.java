package com.employed.bar.infrastructure.config;

import com.employed.bar.domain.event.TestEmailRequestedEvent;
import com.employed.bar.domain.event.WeeklyReportRequestedEvent;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventConfig {

    private final ReportingUseCase reportingUseCase;

    @EventListener
    public void handleWeeklyReportRequest(WeeklyReportRequestedEvent event) {
        reportingUseCase.generateAndSendWeeklyReport(event.getStartDate(), event.getEndDate());
    }

    @EventListener
    public void handleTestEmailRequest(TestEmailRequestedEvent event) {
        reportingUseCase.sendTestEmailToEmployee(event.getEmployeeId());
    }
}
