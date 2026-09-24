package com.datingapp.backend.config;

import com.datingapp.backend.repository.LoginAttemptRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LoginAttemptCleanupTask {

    private final LoginAttemptRepository repository;

    public LoginAttemptCleanupTask(LoginAttemptRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void cleanup() {
        repository.deleteOlderThan(LocalDateTime.now().minusDays(1));
    }
}