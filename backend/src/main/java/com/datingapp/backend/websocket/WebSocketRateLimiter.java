package com.datingapp.backend.websocket;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.time.Instant;

@Component
public class WebSocketRateLimiter{

    private static final int MAX_MESSAGES = 20;
    private static final long WINDOW_SECONDS = 10;

    private final Cache<Long, UserRateLimit> limits =
            Caffeine.newBuilder().expireAfterAccess(Duration.ofMinutes(1)).maximumSize(100_000).build();

    public boolean isAllowed(Long userId){

        Instant now = Instant.now();

        UserRateLimit limit = limits.get(userId, id -> new UserRateLimit(now, 0));

        synchronized (limit){

            long elapsed = now.getEpochSecond() - limit.windowStart.getEpochSecond();

            if (elapsed >= WINDOW_SECONDS){
                limit.windowStart = now;
                limit.messageCount = 0;
            }

            if (limit.messageCount >= MAX_MESSAGES){
                return false;
            }

            limit.messageCount++;

            return true;
        }
    }

    private static class UserRateLimit{

        private Instant windowStart;
        private int messageCount;

        public UserRateLimit(Instant windowStart, int messageCount){
            this.windowStart = windowStart;
            this.messageCount = messageCount;
        }
    }
}