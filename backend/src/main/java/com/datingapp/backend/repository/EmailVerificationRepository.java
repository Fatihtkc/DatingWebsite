package com.datingapp.backend.repository;

import com.datingapp.backend.model.EmailVerification;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long>{

    Optional<EmailVerification> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EmailVerification e WHERE e.email = :email")
    Optional<EmailVerification> findByEmailForUpdate(@Param("email") String email);

    void deleteByEmail(String email);
}