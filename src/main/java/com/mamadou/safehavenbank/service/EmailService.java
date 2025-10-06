package com.mamadou.safehavenbank.service;

import com.mamadou.safehavenbank.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    public void sendEmail(String to, String template,String subject, Context context)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        String message = templateEngine.process(template, context);

        helper.setFrom(from, "SafeHavenBank");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, true);

        mailSender.send(mimeMessage);
    }
    public void sendVerificationEmail(String email,String token,String fullName)
            throws MessagingException, UnsupportedEncodingException {

        String link = "http://localhost:8080/api/auth/verify?token=" + token;
        Context context = new Context();
        context.setVariable("user", fullName);
        context.setVariable("verificationUrl",link);
        sendEmail(email,"verification-email","Verify your email address- SafeHaven Bank",context);

    }
}
