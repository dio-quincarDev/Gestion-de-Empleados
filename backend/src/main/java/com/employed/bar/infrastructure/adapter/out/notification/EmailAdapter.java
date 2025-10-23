package com.employed.bar.infrastructure.adapter.out.notification;

import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;

import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.NotificationPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class EmailAdapter implements NotificationPort {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendReportByEmail(EmployeeClass employee, Report report) {
        String subject = "Your Weekly Report";
        String body = generateEmailBody(employee, report);
        sendHtmlEmail(employee.getEmail(), subject, body);
    }

    private String generateEmailBody(EmployeeClass employee, Report report) {
        Context context = new Context();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("employeeName", employee.getName());
        dataMap.put("attendanceReports", report.getAttendanceLines());
        dataMap.put("individualConsumptionReports", report.getConsumptionLines());
        dataMap.put("totalAttendanceHours", report.getTotalAttendanceHours());
        dataMap.put("totalConsumptionAmount", report.getTotalConsumptionAmount());

        context.setVariables(dataMap);
        return templateEngine.process("weekly-report", context);
    }

    @Override
    public void sendManagerReportByEmail(String managerEmail, ManagerReport managerReport) {
        String subject = "Manager Weekly Report";
        String body = generateManagerEmailBody(managerReport);
        sendHtmlEmail(managerEmail, subject, body);
    }

    private String generateManagerEmailBody(ManagerReport managerReport) {
        Context context = new Context();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("employeeSummaries", managerReport.getEmployeeSummaries());
        dataMap.put("totals", managerReport.getTotals());

        context.setVariables(dataMap);
        return templateEngine.process("manager-weekly-report", context);
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
            // Here you should throw a custom domain exception, e.g., NotificationSendingException
            throw new RuntimeException("Error sending HTML email", e);
        }
    }
}