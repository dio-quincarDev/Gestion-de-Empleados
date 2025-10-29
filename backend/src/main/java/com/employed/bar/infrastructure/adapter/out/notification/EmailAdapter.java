package com.employed.bar.infrastructure.adapter.out.notification;

import com.employed.bar.domain.model.manager.ManagerReport;
import com.employed.bar.domain.model.report.Report;
import com.employed.bar.domain.model.structure.EmployeeClass;
import com.employed.bar.domain.port.out.NotificationPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class EmailAdapter implements NotificationPort {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override

    public void sendReportByEmail(EmployeeClass employee, Report report) {
        System.out.println("📬 [EMAIL ADAPTER] Preparando email para: " + employee.getEmail());
        try {
            String subject = "Your Weekly Report";
            String body = generateEmailBody(employee, report);
            System.out.println("✅ [EMAIL ADAPTER] Cuerpo del email generado, longitud: " + body.length());
            System.out.println("📧 [EMAIL ADAPTER] Contenido preview: " + (body.length() > 100 ? body.substring(0, 100) + "..." : body));

            sendHtmlEmail(employee.getEmail(), subject, body);
            System.out.println("🎉 [EMAIL ADAPTER] EMAIL ENVIADO EXITOSAMENTE");
        } catch (Exception e) {
            System.out.println("❌ [EMAIL ADAPTER] ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error sending email to " + employee.getEmail(), e);
        }
    }
    private String generateEmailBody(EmployeeClass employee, Report report) {
        System.out.println("🛠️ [TEMPLATE] Generando HTML...");

        Context context = new Context();

        // ✅ CORRECTO: Pasar las variables DIRECTAMENTE al context
        context.setVariable("employeeName", employee.getName());
        context.setVariable("attendanceReports", report.getAttendanceLines());
        context.setVariable("individualConsumptionReports", report.getConsumptionLines());
        context.setVariable("totalAttendanceHours", report.getTotalAttendanceHours());
        context.setVariable("totalConsumptionAmount", report.getTotalConsumptionAmount());
        context.setVariable("logoCid", "1800-logo.png"); // Add logo CID

        System.out.println("📋 [TEMPLATE] Datos para plantilla:");
        System.out.println("   - employeeName: " + employee.getName());
        System.out.println("   - attendanceReports: " + report.getAttendanceLines().size() + " registros");
        System.out.println("   - consumptionReports: " + report.getConsumptionLines().size() + " registros");
        System.out.println("   - totalHours: " + report.getTotalAttendanceHours());
        System.out.println("   - totalConsumption: " + report.getTotalConsumptionAmount());

        // DEBUG: Verificar que las variables están en el context
        System.out.println("🔍 [TEMPLATE] Variables en context:");
        context.getVariableNames().forEach(name ->
                System.out.println("   - " + name + ": " + context.getVariable(name))
        );

        try {
            String html = templateEngine.process("weekly-report", context);
            System.out.println("✅ [TEMPLATE] HTML generado exitosamente, longitud: " + html.length());

            // DEBUG CRÍTICO: Ver el contenido REAL del HTML
            System.out.println("📄 [TEMPLATE] CONTENIDO HTML COMPLETO:");
            System.out.println("==========================================");
            System.out.println(html);
            System.out.println("==========================================");

            return html;
        } catch (Exception e) {
            System.out.println("❌ [TEMPLATE] ERROR generando HTML: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generating email template", e);
        }
    }
    @Override
    public void sendManagerReportByEmail(String managerEmail, ManagerReport managerReport) {
        System.out.println("📬 [EMAIL ADAPTER] Preparando email de manager para: " + managerEmail);
        try {
            String subject = "Manager Weekly Report";
            String body = generateManagerEmailBody(managerReport);
            System.out.println("✅ [EMAIL ADAPTER] Cuerpo del email de manager generado, longitud: " + body.length());

            sendHtmlEmail(managerEmail, subject, body);
            System.out.println("🎉 [EMAIL ADAPTER] EMAIL DE MANAGER ENVIADO EXITOSAMENTE");
        } catch (Exception e) {
            System.out.println("❌ [EMAIL ADAPTER] ERROR enviando email de manager: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error sending manager email to " + managerEmail, e);
        }
    }

    private String generateManagerEmailBody(ManagerReport managerReport) {
        System.out.println("🛠️ [TEMPLATE] Generando HTML para manager...");

        Context context = new Context();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("employeeSummaries", managerReport.getEmployeeSummaries());
        dataMap.put("totals", managerReport.getTotals());
        dataMap.put("logoCid", "1800-logo.png"); // Add logo CID

        // ✅ SOLUCIÓN: Pasar el Map al Context
        context.setVariables(dataMap);

        System.out.println("📋 [TEMPLATE] Datos para plantilla de manager:");
        System.out.println("   - employeeSummaries: " + managerReport.getEmployeeSummaries().size() + " empleados");
        System.out.println("   - totals: " + managerReport.getTotals());

        try {
            String html = templateEngine.process("manager-weekly-report", context);
            System.out.println("✅ [TEMPLATE] HTML de manager generado exitosamente, longitud: " + html.length());
            return html;
        } catch (Exception e) {
            System.out.println("❌ [TEMPLATE] ERROR generando HTML de manager: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generating manager email template", e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        System.out.println("📤 [SEND HTML] Enviando email a: " + to);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // 'true' for multipart, 'UTF-8' for encoding
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // 'true' for HTML content

            // Add the inline image
            ClassPathResource clr = new ClassPathResource("static/images/1800-logo.png");
            helper.addInline("1800-logo.png", clr, "image/png"); // Content-ID matches the 'cid:' in HTML

            System.out.println("🔄 [SEND HTML] Ejecutando mailSender.send()...");
            mailSender.send(mimeMessage);
            System.out.println("✅ [SEND HTML] mailSender.send() ejecutado exitosamente");
        } catch (MessagingException e) {
            System.out.println("❌ [SEND HTML] ERROR de Messaging: " + e.getMessage());
            throw new RuntimeException("Error sending HTML email to " + to, e);
        }
    }
}