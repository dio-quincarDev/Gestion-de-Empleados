package com.employed.bar.infrastructure.config;

import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.strucuture.EmployeeClass;
import com.employed.bar.domain.port.out.ReportingPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ReportingAdapter implements ReportingPort {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public Report generateReport(LocalDateTime startDate, LocalDateTime endDate, Long employeeId) {
        // This implementation should be in the ApplicationService. 
        // This adapter should only be concerned with outbound operations like sending emails.
        // This method is misplaced here according to hexagonal architecture.
        throw new UnsupportedOperationException("GenerateReport should not be called on the ReportingAdapter.");
    }

    @Override
    public void sendReportByEmail(EmployeeClass employee, Report report) {
        String subject = "Your Weekly Report";
        String body = generateEmailBody(employee, report);
        sendHtmlEmail(employee.getEmail(), subject, body);
    }

    private String generateEmailBody(EmployeeClass employee, Report report) {
        Context context = new Context();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("employeeName", employee.getName());
        dataMap.put("attendanceLines", report.getAttendanceLines());
        dataMap.put("consumptionLines", report.getConsumptionLines());
        dataMap.put("totalAttendanceHours", report.getTotalAttendanceHours());
        dataMap.put("totalConsumptionAmount", report.getTotalConsumptionAmount());

        context.setVariables(dataMap);
        return templateEngine.process("weekly-report", context);
    }

    private void sendHtmlEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Consider a more specific exception
            throw new RuntimeException("Error sending HTML email", e);
        }
    }

    @Override
    public void sendWeeklyReports(String email, String subject, String body) {
        // Implementation for sending a single report email
        sendEmail(email, subject, body);
    }

    @Override
    public void sendTestEmail(String email, String subject, String body) {
        sendEmail(email, subject, body);
    }

    @Override
    public void sendBulkEmails(List<EmployeeClass> employees, List<Report> reports) {
        // Basic implementation: iterate and send one by one.
        // A more robust solution would use batching or a queue.
        for (int i = 0; i < employees.size(); i++) {
            EmployeeClass employee = employees.get(i);
            Report report = reports.get(i);
            String emailBody = "Here is your report: " + report.toString(); // This should be a formatted template
            sendEmail(employee.getEmail(), "Your Weekly Report", emailBody);
        }
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}





