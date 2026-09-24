package com.datingapp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datingapp.backend.security.ratelimit.RateLimitRule;
import com.datingapp.backend.security.ratelimit.RateLimitRule.KeyStrategy;

import java.time.Duration;
import java.util.List;

@Configuration
public class RateLimitConfig {

    @Bean
    public List<RateLimitRule> rateLimitRules() {
        return List.of(
            new RateLimitRule(
                "login",
                req -> req.getRequestURI().equals("/login") && "POST".equalsIgnoreCase(req.getMethod()),
                5, Duration.ofMinutes(1),
                RateLimitRule.KeyStrategy.IP
            ),

            new RateLimitRule(
                "signup",
                req -> req.getRequestURI().equals("/signup") && "POST".equalsIgnoreCase(req.getMethod()),
                3, Duration.ofMinutes(10),
                RateLimitRule.KeyStrategy.IP
            ),

            new RateLimitRule(
                "check-user",
                req -> req.getRequestURI().equals("/check-user"),
                20, Duration.ofMinutes(1),
                RateLimitRule.KeyStrategy.IP
            ),


            new RateLimitRule(
                "likes",
                req -> req.getRequestURI().equals("/likes") && "POST".equalsIgnoreCase(req.getMethod()),
                30, Duration.ofMinutes(1),
                RateLimitRule.KeyStrategy.USER
            ),

            new RateLimitRule(
                "matches",
                req -> req.getRequestURI().equals("/matches") && "POST".equalsIgnoreCase(req.getMethod()),
                20, Duration.ofMinutes(1),
                RateLimitRule.KeyStrategy.USER
            ),

            new RateLimitRule(
                "image-upload",
                req -> req.getRequestURI().contains("/images")
                    && ("POST".equalsIgnoreCase(req.getMethod()) || "PUT".equalsIgnoreCase(req.getMethod())),
                10, Duration.ofMinutes(1),
                RateLimitRule.KeyStrategy.USER
            )
        );
    }
}