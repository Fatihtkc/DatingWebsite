package com.datingapp.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datingapp.backend.model.Token;

import jakarta.persistence.LockModeType;

public interface TokenRepository extends JpaRepository<Token, String> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Token t WHERE t.hashToken = :tokenHash AND t.type = :type")
    Optional<Token> findByHashTokenAndTypeForUpdate(@Param("tokenHash") String tokenHash, @Param("type") Token.TokenType type);
}

