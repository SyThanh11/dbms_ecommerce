package com.group12.ecommerce.service.interfaceService.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

import java.io.IOException;
import java.util.Map;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailFromTemplate
            (String from, String to, String subject, String template, Map<String, String> placeholders) throws MessagingException, IOException;
}
