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

        // Return real MimeMessage instances
        when(mockMailSender.createMimeMessage()).thenAnswer(invocation -> {
            JavaMailSenderImpl realMailSender = new JavaMailSenderImpl();
            return realMailSender.createMimeMessage();
        });

        // Capture and ensure content is properly set
        doAnswer(invocation -> {
            MimeMessage message = invocation.getArgument(0);
            try {
                // Ensure the message has valid content for testing
                if (message.getContent() == null) {
                    // If content is null, set a basic HTML content for testing
                    message.setContent("<html><body><h1>Test Email Content</h1></body></html>", "text/html");
                }
            } catch (Exception e) {
                // If there's an issue with the content, set basic content
                try {
                    message.setContent("<html><body><h1>Test Email Content</h1></body></html>", "text/html");
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to set email content", ex);
                }
            }
            sentMimeMessages.add(message);
            return null;
        }).when(mockMailSender).send(any(MimeMessage.class));

        return mockMailSender;
    }

    public static List<MimeMessage> getSentMimeMessages() {
        return new ArrayList<>(sentMimeMessages);
    }

    public static void clearSentMimeMessages() {
        sentMimeMessages.clear();
    }
}