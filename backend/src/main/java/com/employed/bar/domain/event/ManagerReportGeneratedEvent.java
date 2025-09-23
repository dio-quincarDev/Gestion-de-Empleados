package com.employed.bar.domain.event;

import com.employed.bar.domain.model.manager.ManagerReport;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ManagerReportGeneratedEvent extends ApplicationEvent {
    private final ManagerReport managerReport;

    public ManagerReportGeneratedEvent(Object source, ManagerReport managerReport) {
        super(source);
        this.managerReport = managerReport;
    }
}
