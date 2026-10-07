package com.datingapp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datingapp.backend.enums.OtpPurpose;
import com.datingapp.backend.model.Otp;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmailAndPurpose(String email,OtpPurpose purpose);
    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
