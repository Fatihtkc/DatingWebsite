package com.datingapp.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import javax.crypto.SecretKey;
import java.util.Base64;

@Component
public class JwtUtil {

    // Bu değerler application.properties üzerinden okunabilir
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    // Secret key'den Key nesnesi oluştur
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // JWT oluşturma
    public String generateJwtToken(UserDetails userDetails) {
        SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret)); // güvenli key oluştur
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 gün geçerli
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Token'dan kullanıcı adını (subject) alma
    public String getUsernameFromJwtToken(String token) {
        Key key = getSigningKey();
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    // Token doğrulama
    public boolean validateJwtToken(String token) {
        try {
            Key key = getSigningKey();
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Hataları loglayabilirsiniz
        }
        return false;
    }
}
