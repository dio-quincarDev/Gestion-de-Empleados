package com.employed.bar.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

@Getter
public class ManagerReportRequestedEvent extends ApplicationEvent {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public ManagerReportRequestedEvent(Object source, LocalDate startDate, LocalDate endDate) {
        super(source);
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
