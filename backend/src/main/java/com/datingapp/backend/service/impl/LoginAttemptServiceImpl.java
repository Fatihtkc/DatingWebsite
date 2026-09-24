package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.LoginAttempt;
import com.datingapp.backend.repository.LoginAttemptRepository;
import com.datingapp.backend.service.LoginAttemptService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor 
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository repository;

    @Value("${app.security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${app.security.lockout-window-minutes:15}")
    private int lockoutWindowMinutes;

    @Override
    @Transactional
    public void recordAttempt(String email, String ip, boolean successful){
        LocalDateTime now = LocalDateTime.now();

        repository.save(new LoginAttempt(null, email, "EMAIL", now, successful));
        repository.save(new LoginAttempt(null, ip, "IP", now, successful));

        if (successful) {
            repository.clearAttempts(email);
        }
    }

    @Override
    public boolean isLocked(String email, String ip){
        LocalDateTime since = LocalDateTime.now().minusMinutes(lockoutWindowMinutes);

        long emailFailures = repository.countRecentFailures(email, since);
        long ipFailures = repository.countRecentFailures(ip, since);

        return emailFailures >= maxFailedAttempts || ipFailures >= (maxFailedAttempts * 3L);
    }
}