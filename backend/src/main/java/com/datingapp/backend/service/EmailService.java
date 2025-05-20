package com.datingapp.backend.service;

import com.datingapp.backend.model.Token;

public interface EmailService {
    void sendTokenEmail(String to, String token, Token.TokenType type);
    void sendOtpEmail(String toEmail, String otp);
}
