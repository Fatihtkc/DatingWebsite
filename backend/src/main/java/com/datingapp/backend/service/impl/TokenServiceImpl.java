package com.datingapp.backend.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.model.Token;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.TokenRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createToken(User user, String token, Token.TokenType type, Duration validity) {
        Token t = new Token();
        t.setToken(token);
        t.setUser(user);
        t.setType(type);
        t.setExpiryDate(LocalDateTime.now().plus(validity));
        tokenRepository.save(t);
    }

    @Override
    public User validateToken(String token, Token.TokenType type) {
        return tokenRepository.findByTokenAndType(token, type)
            .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
            .map(Token::getUser)
            .orElseThrow(() -> new IllegalArgumentException("Token geçersiz veya süresi dolmuş"));
    }

    @Override
    public void invalidateToken(String token) {
        tokenRepository.deleteById(token);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        User user = validateToken(token, Token.TokenType.PASSWORD_RESET);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        invalidateToken(token);
    }

    @Override
    public void createPasswordResetToken(String email, String token, Duration duration) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        createToken(user, token, Token.TokenType.PASSWORD_RESET, duration);
    }
    
}

