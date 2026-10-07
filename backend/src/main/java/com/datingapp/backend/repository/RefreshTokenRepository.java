package com.datingapp.backend.repository;

import com.datingapp.backend.model.RefreshToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.familyId = :familyId")
    void revokeFamily(@Param("familyId") String familyId);

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.subject = :subject")
    void revokeAllBySubject(@Param("subject") String subject);

    @Modifying
    @Query("delete from RefreshToken r where r.expiryDate < :now")
    void deleteExpired(@Param("now") LocalDateTime now);
}