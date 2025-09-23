package com.employed.bar.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TestEmailRequestedEvent extends ApplicationEvent {
    private final Long employeeId;

    public TestEmailRequestedEvent(Object source, Long employeeId) {
        super(source);
        this.employeeId = employeeId;
    }
}
