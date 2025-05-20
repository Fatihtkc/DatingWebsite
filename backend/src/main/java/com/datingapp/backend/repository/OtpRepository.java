package com.datingapp.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.datingapp.backend.model.Otp;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByEmail(String email);
    void deleteByEmail(String email);
}
