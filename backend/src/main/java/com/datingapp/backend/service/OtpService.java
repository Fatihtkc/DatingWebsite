package com.datingapp.backend.service;

public interface OtpService {
    String generateOtp();
    void storeOtp(String email, String otp);
    boolean verifyOtp(String email, String code);
    void clearOtp(String email);
}
