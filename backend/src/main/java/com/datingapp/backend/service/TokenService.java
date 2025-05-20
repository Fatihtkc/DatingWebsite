package com.datingapp.backend.service;

import com.datingapp.backend.model.Token;
import com.datingapp.backend.model.User;
import java.time.Duration;

public interface TokenService {
    void createToken(User user, String token, Token.TokenType type, Duration validity);
    User validateToken(String token, Token.TokenType type);
    void invalidateToken(String token);
    void createPasswordResetToken(String email, String token, Duration duration);
    void resetPassword(String token, String newPassword);
}
