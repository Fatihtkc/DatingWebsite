package com.datingapp.backend.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datingapp.backend.exception.InvalidOtpException;
import com.datingapp.backend.exception.InvalidTokenTypeException;
import com.datingapp.backend.exception.TooManyRequestsException;
import com.datingapp.backend.model.EmailVerification;
import com.datingapp.backend.model.Token;
import com.datingapp.backend.repository.EmailVerificationRepository;
import com.datingapp.backend.security.ratelimit.OtpSendRateLimiter;
import com.datingapp.backend.service.EmailService;
import com.datingapp.backend.service.OtpService;
import com.datingapp.backend.service.TokenService;
import com.datingapp.backend.service.VerificationService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final OtpService otpService;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final OtpSendRateLimiter otpSendRateLimiter;
    private final EmailVerificationRepository emailVerificationRepository;


    private final Cache<String, Bucket> passwordResetBuckets = Caffeine.newBuilder().maximumSize(100_000).expireAfterAccess(Duration.ofMinutes(15)).build();


    @Override
    public void sendVerificationCode(String email){

        String normalizedEmail = email.trim().toLowerCase();

        otpSendRateLimiter.checkAndRecord(normalizedEmail);

        String otp = otpService.generateOtp();

        otpService.storeOtp(normalizedEmail, otp);

        emailService.sendOtpEmail(normalizedEmail, otp);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {

        String normalizedEmail = email.trim().toLowerCase(java.util.Locale.ROOT);

        boolean valid = otpService.verifyOtp(normalizedEmail, otp);

        if(!valid){
            throw new InvalidOtpException("Invalid or expired OTP.");
        }

        LocalDateTime now = LocalDateTime.now();

        EmailVerification verification = emailVerificationRepository.findByEmail(normalizedEmail).orElseGet(EmailVerification::new);

        verification.setEmail(normalizedEmail);
        verification.setVerifiedAt(now);
        verification.setExpiresAt(now.plusMinutes(15));

        emailVerificationRepository.save(verification);
    }

    @Override
    public void forgotPassword(String email){

        String normalizedEmail = email.trim().toLowerCase();

        Bucket bucket = passwordResetBuckets.get(
            normalizedEmail,
            key -> Bucket.builder().addLimit(Bandwidth.builder().capacity(3).refillGreedy(3, Duration.ofMinutes(15)).build()).build()
        );

        if(!bucket.tryConsume(1)){
            throw new TooManyRequestsException("Too many password reset requests. Please try again later.");
        }

        String token = UUID.randomUUID().toString();

        tokenService.createPasswordResetToken(normalizedEmail,token, Duration.ofMinutes(15));

        emailService.sendTokenEmail(normalizedEmail, token, Token.TokenType.PASSWORD_RESET);
    }

    @Override
    public void validateToken(String token, String type){

        Token.TokenType tokenType;

        try {
            tokenType = Token.TokenType.valueOf(type);
        } catch (IllegalArgumentException e){
            throw new InvalidTokenTypeException("Invalid token type.");
        }

        tokenService.validateToken(token, tokenType);
    }

    @Override
    public void resetPassword(String token, String newPassword){

        tokenService.resetPassword(token, newPassword);
    }
}