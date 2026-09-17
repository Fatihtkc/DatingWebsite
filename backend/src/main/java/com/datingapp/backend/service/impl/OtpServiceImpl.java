package com.datingapp.backend.service.impl;

import org.springframework.stereotype.Service;

import com.datingapp.backend.model.Otp;
import com.datingapp.backend.repository.OtpRepository;
import com.datingapp.backend.service.OtpService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
    
    @Override
    public void storeOtp(String email, String otp) {
        otpRepository.deleteByEmail(email); 
        Otp otpEntity = new Otp(null, email, otp, LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otpEntity);
    }
    
    @Override
    public boolean verifyOtp(String email, String code) {
        Optional<Otp> optionalOtp = otpRepository.findByEmail(email);
        if (optionalOtp.isPresent()) {
            Otp otp = optionalOtp.get();
            return otp.getCode().equals(code) && otp.getExpiry().isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    @Transactional
    public void clearOtp(String email) {
        otpRepository.deleteByEmail(email);
    }
}
