package com.employed.bar.infrastructure.mail;

import jakarta.mail.internet.MimeMessage;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestMailConfig {

    private static final List<MimeMessage> sentMimeMessages = new ArrayList<>();

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSender mockMailSender = Mockito.mock(JavaMailSender.class);
        JavaMailSenderImpl realMailSender = new JavaMailSenderImpl();

        // When createMimeMessage is called, return a real MimeMessage
        when(mockMailSender.createMimeMessage()).thenAnswer(invocation -> realMailSender.createMimeMessage());

        // When send is called, capture the MimeMessage with its actual content
        doAnswer(invocation -> {
            MimeMessage originalMessage = invocation.getArgument(0);

            try {
                // Crear una copia COMPLETA del mensaje
                MimeMessage capturedMessage = realMailSender.createMimeMessage();

                // Copiar TODO el contenido y propiedades
                capturedMessage.setContent(originalMessage.getContent(), originalMessage.getContentType());
                capturedMessage.setSubject(originalMessage.getSubject());
                capturedMessage.setRecipients(MimeMessage.RecipientType.TO, originalMessage.getRecipients(MimeMessage.RecipientType.TO));

                // Forzar la serialización del contenido
                capturedMessage.saveChanges();

                sentMimeMessages.add(capturedMessage);
                System.out.println("✅ [TEST MAIL] Email capturado - Subject: " + capturedMessage.getSubject());
                System.out.println("✅ [TEST MAIL] Content Type: " + capturedMessage.getContentType());
            } catch (Exception e) {
                System.out.println("❌ [TEST MAIL] Error capturando email: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }).when(mockMailSender).send(any(MimeMessage.class));
        return mockMailSender;
    }

    public static List<MimeMessage> getSentMimeMessages() {
        return new ArrayList<>(TestMailConfig.sentMimeMessages);
    }

    public static void clearSentMimeMessages() {
        sentMimeMessages.clear();
    }
}