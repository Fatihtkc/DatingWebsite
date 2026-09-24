package com.datingapp.backend.repository;

import com.datingapp.backend.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.identifier = :identifier AND la.successful = false AND la.attemptedAt > :since")
    long countRecentFailures(@Param("identifier") String identifier, @Param("since") LocalDateTime since);

    @Modifying
    @Query("DELETE FROM LoginAttempt la WHERE la.identifier = :identifier")
    void clearAttempts(@Param("identifier") String identifier);

    @Modifying
    @Query("DELETE FROM LoginAttempt la WHERE la.attemptedAt < :before")
    void deleteOlderThan(@Param("before") LocalDateTime before);
}