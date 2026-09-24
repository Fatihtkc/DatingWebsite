package com.datingapp.backend.service;

public interface VerificationService{

    void sendVerificationCode(String email);
    void verifyOtp(String email, String code);
    void forgotPassword(String email);
    void validateToken(String token, String type);
    void resetPassword(String token, String newPassword);
}