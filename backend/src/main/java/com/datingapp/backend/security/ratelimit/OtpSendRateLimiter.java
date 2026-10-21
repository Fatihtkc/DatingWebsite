
package com.datingapp.backend.security.ratelimit;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
public class OtpSendRateLimiter{

    private static final int MAX_EMAIL_10_MIN = 3;
    private static final int MAX_EMAIL_24_HOURS = 10;
    private static final int MAX_GLOBAL_PER_MINUTE = 30;

    private static final Duration COOLDOWN = Duration.ofSeconds(60);
    private static final Duration TEN_MINUTES = Duration.ofMinutes(10);
    private static final Duration ONE_DAY = Duration.ofHours(24);
    private static final Duration ONE_MINUTE = Duration.ofMinutes(1);

    private final Cache<String, EmailState> emailStates =
            Caffeine.newBuilder().expireAfterAccess(Duration.ofHours(25)).maximumSize(100_000).build();

    private Instant globalWindowStart = Instant.now();
    private int globalCount = 0;

    public synchronized void checkAndRecord(String email){

        Instant now = Instant.now();

        if (Duration.between(globalWindowStart, now).compareTo(ONE_MINUTE) >= 0){
            globalWindowStart = now;
            globalCount = 0;
        }

        if (globalCount >= MAX_GLOBAL_PER_MINUTE){
            throw tooManyRequests();
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        EmailState state = emailStates.get(normalizedEmail, key -> new EmailState());

        state.sentAt.removeIf(time -> Duration.between(time, now).compareTo(ONE_DAY) >= 0);

        if (state.lastSentAt != null && Duration.between(state.lastSentAt, now).compareTo(COOLDOWN) < 0){
            throw tooManyRequests();
        }

        long last10Minutes = state.sentAt.stream().filter(time -> Duration.between(time, now).compareTo(TEN_MINUTES) < 0).count();

        if (last10Minutes >= MAX_EMAIL_10_MIN){
            throw tooManyRequests();
        }

        if (state.sentAt.size() >= MAX_EMAIL_24_HOURS){
            throw tooManyRequests();
        }

        state.sentAt.addLast(now);
        state.lastSentAt = now;
        globalCount++;
    }

    private ResponseStatusException tooManyRequests(){
        return new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "Please wait before requesting another verification code."
        );
    }

    private static class EmailState{
        private final Deque<Instant> sentAt = new ArrayDeque<>();
        private Instant lastSentAt;
    }
}
