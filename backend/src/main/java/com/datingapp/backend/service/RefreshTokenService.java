package com.datingapp.backend.service;

public interface RefreshTokenService {

    String create(String subject);                 
    AuthTokens refresh(String rawRefreshToken);    
    void revoke(String rawRefreshToken);           
    void revokeAll(String subject);               

    record AuthTokens(Long id, String username, String accessToken,
                      String refreshToken, com.datingapp.backend.enums.Role role) {}
}