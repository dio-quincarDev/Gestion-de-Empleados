package com.employed.bar.infrastructure.config;

import com.employed.bar.domain.port.in.service.ManagerReportServicePort;
import com.employed.bar.infrastructure.adapter.in.scheduler.WeeklyReportScheduler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public WeeklyReportScheduler weeklyReportScheduler(
            ApplicationEventPublisher eventPublisher,
            ManagerReportServicePort managerReportServicePort) {
        return new WeeklyReportScheduler(eventPublisher, managerReportServicePort);
    }
}
