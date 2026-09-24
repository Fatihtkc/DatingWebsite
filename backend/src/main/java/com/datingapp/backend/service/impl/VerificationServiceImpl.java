package com.datingapp.backend.service.impl;

import java.time.Duration;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.datingapp.backend.exception.InvalidOtpException;
import com.datingapp.backend.exception.InvalidTokenTypeException;
import com.datingapp.backend.model.Token;
import com.datingapp.backend.service.EmailService;
import com.datingapp.backend.service.OtpService;
import com.datingapp.backend.service.TokenService;
import com.datingapp.backend.service.VerificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final OtpService otpService;
    private final EmailService emailService;
    private final TokenService tokenService;

    @Override
    public void sendVerificationCode(String email){

        String otp = otpService.generateOtp();

        otpService.storeOtp(email, otp);

        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public void verifyOtp(String email, String code){

        boolean valid = otpService.verifyOtp(email, code);

        if (!valid) {
            throw new InvalidOtpException("Invalid or expired OTP.");
        }

        otpService.clearOtp(email);
    }

    @Override
    public void forgotPassword(String email) {

        String token = UUID.randomUUID().toString();

        tokenService.createPasswordResetToken(email, token, Duration.ofHours(1));

        emailService.sendTokenEmail(email, token, Token.TokenType.PASSWORD_RESET);
    }

    @Override
    public void validateToken(String token, String type) {

        Token.TokenType tokenType;

        try {
            tokenType = Token.TokenType.valueOf(type);
        } catch (IllegalArgumentException e){
            throw new InvalidTokenTypeException("Invalid token type.");
        }

        tokenService.validateToken(token, tokenType);
    }

    @Override
    public void resetPassword(String token, String newPassword) {

        tokenService.resetPassword(token, newPassword);
    }
}