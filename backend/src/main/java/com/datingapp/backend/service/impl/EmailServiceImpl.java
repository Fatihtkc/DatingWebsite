package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Token;
import com.datingapp.backend.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendTokenEmail(String to, String token, Token.TokenType type) {
        String subject;
        String link;

        subject = "Password Reset";
        link = "http://localhost:3000/changepassword/" + token; // Şifre sıfırlama linki

        String body = "Click on this link to complete the process:\n\n" + link;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("soulm.dating@gmail.com");

        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your verification code is: " + otp + "\nThis code will expire in 5 minutes.");
        mailSender.send(message);
    }
}
