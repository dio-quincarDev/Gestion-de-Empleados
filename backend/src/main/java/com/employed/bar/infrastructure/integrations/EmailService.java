package com.employed.bar.infrastructure.integrations;
import com.employed.bar.infrastructure.dto.ReportDto;
import com.employed.bar.domain.exceptions.EmailSendingException;
import com.employed.bar.domain.model.Employee;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public String generateEmailBody(ReportDto report, Employee employee) {
        Context context = new Context();
        // Coloca todos los datos del reporte en el contexto de Thymeleaf
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("employeeName", employee.getName());
        dataMap.put("attendanceReports", report.getAttendanceReports());
        dataMap.put("individualConsumptionReports", report.getIndividualConsumptionReports());
        dataMap.put("totalAttendanceHours", report.getTotalAttendanceHours());
        dataMap.put("totalConsumptionAmount", report.getTotalConsumptionAmount());

        context.setVariables(dataMap);  // Pasa todo el Map a Thymeleaf

        // Procesar la plantilla con Thymeleaf
        return templateEngine.process("weekly-report", context);
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            mailSender.send(message);
        }catch (Exception e) {
            throw new EmailSendingException("Error al enviar correo Simple a " + to, e);
        }
    }

    public void sendHtmlMessage(String to, String subject, String body){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body,true);
            mailSender.send(mimeMessage);

        } catch(MessagingException e){
            throw new EmailSendingException("Error al Enviar Correo",e);
        }
    }

    public CompletableFuture<Void>sendEmailAsync(Employee employee, ReportDto reportDto){
        return CompletableFuture.runAsync(() -> {
            try{
                String emailBody = generateEmailBody(reportDto, employee);
                String emailSubject = "Weekly Report " + employee.getName();
                sendHtmlMessage(employee.getEmail(), emailSubject, emailBody);
            }catch(Exception e){
                throw new EmailSendingException("Error al Enviar Correo asincrónico a " + employee.getEmail(), e);
            }
        });
    }

}
