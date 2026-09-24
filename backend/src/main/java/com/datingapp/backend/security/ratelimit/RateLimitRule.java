package com.datingapp.backend.security.ratelimit;

import java.time.Duration;
import java.util.function.Predicate;
import jakarta.servlet.http.HttpServletRequest;

public record RateLimitRule(
    String name,                          
    Predicate<HttpServletRequest> matcher, 
    int capacity,                          
    Duration window,                       
    KeyStrategy keyStrategy                
) {
    public enum KeyStrategy { IP, USER }
}