package com.datingapp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.datingapp.backend.enums.OtpPurpose;
import com.datingapp.backend.model.Otp;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Otp o WHERE o.email = :email AND o.purpose = :purpose")
    Optional<Otp> findByEmailAndPurposeForUpdate(@Param("email") String email, @Param("purpose") OtpPurpose purpose);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
