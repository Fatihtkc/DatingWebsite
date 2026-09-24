package com.datingapp.backend.service;

public interface LoginAttemptService {

    void recordAttempt(String email, String ip, boolean successful);
    boolean isLocked(String email, String ip);
}