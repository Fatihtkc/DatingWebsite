package com.datingapp.backend.service;

public interface VerificationService{

    void sendVerificationCode(String email);
    void verifyOtp(String email, String otp);
    void forgotPassword(String email);
    void validateToken(String token, String type);
    void resetPassword(String token, String newPassword);
}