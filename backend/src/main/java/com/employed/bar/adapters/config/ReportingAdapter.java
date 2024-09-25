package com.employed.bar.adapters.config;

import com.employed.bar.adapters.dtos.AttendanceReportDto;
import com.employed.bar.adapters.dtos.ConsumptionReportDto;
import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.Consumption;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ConsumptionRepository;
import com.employed.bar.ports.in.EmployeeRepository;
import com.employed.bar.ports.in.ReportingPort;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@Component
public class ReportingAdapter implements ReportingPort {
    private final ReportingService reportingService;
@Override
    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        return reportingService.generateCompleteReport(startDate.toLocalDate(), employeeId);
    }

}
