package com.datingapp.backend.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datingapp.backend.model.Token;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.TokenRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.RefreshTokenService;
import com.datingapp.backend.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;


    @Override
    @Transactional
    public void createToken(User user, String token, Token.TokenType type, Duration validity){

        LocalDateTime expiryDate = LocalDateTime.now().plus(validity);

        Token t = new Token();
        t.setHashToken(hash(token));
        t.setUser(user);
        t.setType(type);
        t.setExpiryDate(expiryDate);
        tokenRepository.save(t);
    }

    @Override
    @Transactional(readOnly = true)
    public User validateToken(String token, Token.TokenType type){

        String tokenHash = hash(token);

        LocalDateTime now = LocalDateTime.now();

        return tokenRepository.findByHashTokenAndTypeForUpdate(tokenHash, type)
            .filter(t -> t.getExpiryDate().isAfter(now)).map(t -> t.getUser())
            .orElseThrow(() -> new IllegalArgumentException("The token is invalid or has expired."));
    }

    @Override
    @Transactional 
    public void invalidateToken(String hashToken){
        tokenRepository.deleteById(hashToken);
    }

    @Override
    @Transactional 
    public void resetPassword(String token, String newPassword){

        String tokenHash = hash(token);

        Token storedToken = tokenRepository.findByHashTokenAndTypeForUpdate(tokenHash, Token.TokenType.PASSWORD_RESET)
            .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new IllegalArgumentException("The token is invalid or has expired."));

        User user = storedToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        refreshTokenService.revokeAll(user.getEmail());
        tokenRepository.delete(storedToken);
    }

    @Override
    public void createPasswordResetToken(String email, String token, Duration duration){
        userRepository.findByEmail(email).ifPresent(user -> createToken(user, token, Token.TokenType.PASSWORD_RESET, duration));
    }

    private String hash(String raw){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e){
            throw new IllegalStateException(e);
        }
    }
    
}

