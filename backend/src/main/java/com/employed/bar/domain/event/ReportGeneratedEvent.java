package com.employed.bar.domain.event;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class ReportGeneratedEvent extends ApplicationEvent {
    private final List<EmployeeClass> employees;
    private final List<Report> reports;

    public ReportGeneratedEvent(Object source, List<EmployeeClass> employees, List<Report> reports) {
        super(source);
        this.employees = employees;
        this.reports = reports;
    }
}
