package com.group12.ecommerce.service.email;

import com.group12.ecommerce.service.implementService.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendEmailFromTemplate() throws MessagingException, IOException {

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Call the method
        emailService.sendEmailFromTemplate(
                "no-reply@ecommerce.com",
                "sythanhdev@gmail.com",
                "Test Email",
                "templates/htmlForgotPasswordTemplate.html",
                Map.of("name", "John Doe", "newPassword", "123456")
        );

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
