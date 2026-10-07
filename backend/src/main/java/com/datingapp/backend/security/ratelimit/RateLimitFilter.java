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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter{

    private final List<RateLimitRule> rules;

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder().maximumSize(100_000).expireAfterAccess(Duration.ofMinutes(10)).build();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        for (RateLimitRule rule : rules){
            if (rule.matcher().test(request)){
                String bucketKey = rule.name() + ":" + resolveKey(request, rule.keyStrategy());
                Bucket bucket = buckets.get(bucketKey, k -> newBucket(rule));

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

        switch (strategy){

            case USER:
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();

                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())){
                    return auth.getName();
                }

                return resolveIp(request);

            case EMAIL:
                String email = request.getParameter("email");

                if (email != null && !email.isBlank()){
                    return email.trim().toLowerCase();
                }

                return resolveIp(request);

            case IP:
            default:
                return resolveIp(request);
        }
    }

    private String resolveIp(HttpServletRequest request){ 
        return request.getRemoteAddr();
    }
}