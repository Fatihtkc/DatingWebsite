package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.RefreshToken;
import com.datingapp.backend.repository.RefreshTokenRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsServiceImpl userDetailsService;

    @Value("${jwt.refresh-token-ttl}")
    private Duration refreshTokenTtl;

    @Override
    @Transactional
    public String create(String subject) {
        return issue(subject, UUID.randomUUID().toString());
    }

    @Override
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public AuthTokens refresh(String rawToken) {

        RefreshToken stored = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (stored.isRevoked()) {
            repository.revokeFamily(stored.getFamilyId());
            throw new BadCredentialsException("Refresh token reuse detected");
        }

        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(stored.getSubject());

        if (!userDetails.isAccountNonLocked()) {
            repository.revokeFamily(stored.getFamilyId());
            throw new BadCredentialsException("Account is locked");
        }

        stored.setRevoked(true);
        repository.save(stored);

        String newRefresh = issue(stored.getSubject(), stored.getFamilyId());
        String newAccess = jwtUtil.generateJwtToken(userDetails);

        CustomUserDetails cud = (CustomUserDetails) userDetails;
        return new AuthTokens(cud.getId(), cud.getUsername(), newAccess, newRefresh, cud.getRole());
    }

    @Override
    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenHash(hash(rawToken)).ifPresent(t -> {repository.revokeFamily(t.getFamilyId());});
    }

    @Override
    @Transactional
    public void revokeAll(String subject){
        repository.revokeAllBySubject(subject);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanup(){
        repository.deleteExpired(LocalDateTime.now());
    }

    private String issue(String subject, String familyId){
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken rt = new RefreshToken();
        rt.setTokenHash(hash(raw));
        rt.setSubject(subject);
        rt.setFamilyId(familyId);
        rt.setExpiryDate(LocalDateTime.now().plus(refreshTokenTtl));
        repository.save(rt);

        return raw;
    }

    private String hash(String raw){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}