package com.group12.ecommerce.service.implementService.email;

import com.group12.ecommerce.service.interfaceService.email.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService implements IEmailService {

    @Autowired
    JavaMailSender mailSender;

    @Async
    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Async
    @Override
    public CompletableFuture<Boolean> sendEmailFromTemplate
            (String from, String to, String subject, String template, Map<String, String> placeholders)
            throws MessagingException, IOException {
        log.info("📧 Bắt đầu gửi email template đến: {}", to);
        try {
            String htmlContent = loadTemplate(template);
            if (htmlContent == null) {
                log.error("❌ Không tìm thấy template email: {}", template);
                return CompletableFuture.completedFuture(false);
            }

            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                htmlContent = htmlContent.replace("${" + entry.getKey() + "}", entry.getValue());
            }

            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(from));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            mailSender.send(message);
            log.info("✅ Email template đã gửi thành công đến: {}", to);
            return CompletableFuture.completedFuture(true);
        } catch (MessagingException e) {
            log.error("❌ Lỗi gửi email template đến {}: {}", to, e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    private String loadTemplate(String filePath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            log.error("Error reading email template: {}", filePath, e);
            return null;
        }
    }
}


