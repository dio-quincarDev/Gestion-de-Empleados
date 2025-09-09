package com.employed.bar.infrastructure.config;

import com.employed.bar.infrastructure.dto.ReportDto;
import com.employed.bar.domain.model.EmployeeClass;
import com.employed.bar.domain.port.in.service.ReportingUseCase;
import com.employed.bar.domain.port.out.ReportingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Component
public class ReportingAdapter implements ReportingPort {
    private final ReportingUseCase reportingUseCase;
    private final JavaMailSender mailSender;

    @Override
    public ReportDto generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        return reportingUseCase.generateCompleteReport(startDate.toLocalDate(),endDate.toLocalDate(), employeeId);
    }

    @Override
    public void sendWeeklyReports(String email, String subject, String body) {
        reportingUseCase.sendWeeklyReports();
    }

    @Override
    public void sendTestEmail(String email, String subject, String body) {
        reportingUseCase.sendTestEmail();
    }

    @Override
    public void sendBulkEmails(List<EmployeeClass> employees, List<ReportDto> reports) {
        reportingUseCase.sendBulkEmails(employees, reports);
    }

}





