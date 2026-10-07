package com.datingapp.backend.service;

public interface OtpHashService{
    String hash(String email, String otp, String purpose);
}
