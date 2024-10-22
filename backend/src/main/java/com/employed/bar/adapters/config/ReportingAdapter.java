package com.employed.bar.adapters.config;

import com.employed.bar.adapters.dtos.ReportDto;
import com.employed.bar.domain.model.Employee;
import com.employed.bar.domain.services.ReportingService;
import com.employed.bar.ports.in.ReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Component
public class ReportingAdapter implements ReportingPort {
    private final ReportingService reportingService;
    private final JavaMailSender mailSender;

    @Override
    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        return reportingService.generateCompleteReport(startDate.toLocalDate(), employeeId);
    }

    @Override
    public void sendWeeklyReports(String email, String subject, String body) {
        reportingService.sendWeeklyReports();
    }

    @Override
    public void sendTestEmail(String email, String subject, String body) {
        reportingService.sendTestEmail();
    }

    @Override
    public void sendBulkEmails(List<Employee> employees, List<ReportDto> reports) {
        reportingService.sendBulkEmails(employees, reports);
    }

}





