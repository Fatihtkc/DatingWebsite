package com.datingapp.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datingapp.backend.model.Token;

public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByTokenAndType(String token, Token.TokenType type);
}

