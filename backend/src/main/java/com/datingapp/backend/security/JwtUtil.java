package com.datingapp.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;
import javax.crypto.SecretKey;

import java.time.Duration;
import java.util.Base64;

@Component
public class JwtUtil{

    @Value("${jwt.access-token-ttl}")
    private Duration accessTokenTtl;

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private SecretKey getSigningKey(){
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(UserDetails userDetails){

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenTtl.toMillis()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

    }

    public String getUsernameFromJwtToken(String token){

        return Jwts.parserBuilder()
                   .setSigningKey(getSigningKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();

    }

    public boolean validateJwtToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
