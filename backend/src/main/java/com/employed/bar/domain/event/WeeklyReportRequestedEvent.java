package com.employed.bar.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class WeeklyReportRequestedEvent extends ApplicationEvent {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public WeeklyReportRequestedEvent(Object source, LocalDate startDate, LocalDate endDate) {
        super(source);
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
