package com.employed.bar.infrastructure.adapter.in.scheduler;

import com.employed.bar.domain.event.WeeklyReportRequestedEvent;
import com.employed.bar.domain.port.in.service.ManagerReportServicePort;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeeklyReportScheduler {

    private final ApplicationEventPublisher eventPublisher;
    private final ManagerReportServicePort managerReportServicePort;

    public WeeklyReportScheduler(ApplicationEventPublisher eventPublisher, ManagerReportServicePort managerReportServicePort) {
        this.eventPublisher = eventPublisher;
        this.managerReportServicePort = managerReportServicePort;
    }


    /**
     * This scheduler runs every Monday at 7 AM.
     * It triggers the generation of a weekly report for the past week (Monday to Sunday).
     */
    @Scheduled(cron = "0 0 7 * * MON")
    public void triggerWeeklyReportGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(DayOfWeek.MONDAY).minusWeeks(1);
        LocalDate endDate = today.with(DayOfWeek.SUNDAY);

        // Trigger employee reports
        WeeklyReportRequestedEvent employeeEvent = new WeeklyReportRequestedEvent(this, startDate, endDate);
        eventPublisher.publishEvent(employeeEvent);

        // Trigger manager report
        managerReportServicePort.generateAndSendManagerReport(startDate, endDate);
    }
}
