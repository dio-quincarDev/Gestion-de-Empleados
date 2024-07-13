package com.employed.bar.adapters.integrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;
    public void sendNotification(String to, String subject, String message){
        emailService.sendSimpleMessage(to, subject, message);
    }
    // Otros
    // métodos adicionales para otras formas de notificación, como SMS, push notifications, etc.

}
