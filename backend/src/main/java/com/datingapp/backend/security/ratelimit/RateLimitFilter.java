package com.datingapp.backend.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter{

    private final List<RateLimitRule> rules;

    private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        for (RateLimitRule rule : rules){
            if (rule.matcher().test(request)){
                String bucketKey = rule.name() + ":" + resolveKey(request, rule.keyStrategy());
                Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> newBucket(rule));

                if (!bucket.tryConsume(1)){
                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\":\"Too many requests. Please try again later.\"}"
                    );
                    return; 
                }

                break; 
            }
        }

        filterChain.doFilter(request, response);
    }

    private Bucket newBucket(RateLimitRule rule) {

        Bandwidth limit = Bandwidth.builder().capacity(rule.capacity()).refillGreedy(rule.capacity(), rule.window()).build();

        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveKey(HttpServletRequest request, RateLimitRule.KeyStrategy strategy){

        if (strategy == RateLimitRule.KeyStrategy.USER){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())){
                return auth.getName();
            }
            return resolveIp(request);
        }

        return resolveIp(request);
    }

    private String resolveIp(HttpServletRequest request){

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        
        return request.getRemoteAddr();
    }
}