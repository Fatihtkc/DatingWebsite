package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Token;
import com.datingapp.backend.service.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void sendTokenEmail(String to, String token, Token.TokenType type) {

        String resetLink = UriComponentsBuilder.fromUriString(frontendUrl).path("/reset-password").queryParam("token", token).build().encode().toUriString(); 

        String body = "Click on this link to complete the process:\n\n" + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText(body);
        message.setFrom("soulm.dating@gmail.com");

        mailSender.send(message);
    }

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Your verification code is: " + otp + "\nThis code will expire in 10 minutes.");
        mailSender.send(message);
    }
}
