package com.employed.bar.application.service;

import com.employed.bar.domain.event.ManagerReportRequestedEvent;
import com.employed.bar.domain.event.WeeklyReportRequestedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * This scheduler runs every Sunday at 10 PM.
     * It triggers the generation of a weekly report for the past week (Monday to Sunday).
     */
    @Scheduled(cron = "0 0 22 * * SUN")
    public void triggerWeeklyReportGeneration() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(DayOfWeek.MONDAY).minusWeeks(1);
        LocalDate endDate = today.with(DayOfWeek.SUNDAY);

        // Trigger employee reports
        WeeklyReportRequestedEvent employeeEvent = new WeeklyReportRequestedEvent(this, startDate, endDate);
        eventPublisher.publishEvent(employeeEvent);

        // Trigger manager report
        ManagerReportRequestedEvent managerEvent = new ManagerReportRequestedEvent(this, startDate, endDate);
        eventPublisher.publishEvent(managerEvent);
    }
}
