package com.datingapp.backend.service.impl;

import org.springframework.stereotype.Service;

import com.datingapp.backend.enums.OtpPurpose;
import com.datingapp.backend.model.Otp;
import com.datingapp.backend.repository.OtpRepository;
import com.datingapp.backend.service.OtpHashService;
import com.datingapp.backend.service.OtpService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final OtpHashService otpHashService;
    private static final int MAX_ATTEMPTS = 5;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp(){
        return String.format("%06d", random.nextInt(1000000));
    }
    
    @Override
    @Transactional 
    public void storeOtp(String email, String otp){
        otpRepository.deleteByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION); 
        String codeHash = otpHashService.hash(email, otp, OtpPurpose.EMAIL_VERIFICATION.name());
        Otp otpEntity = new Otp(null, email, codeHash, OtpPurpose.EMAIL_VERIFICATION, 0, LocalDateTime.now().plusMinutes(10));
        otpRepository.save(otpEntity);
    }
    
    @Override
    public boolean verifyOtp(String email, String otp){
        Otp storedOtp = otpRepository.findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION).orElse(null);

        if (storedOtp == null){
            return false;
        }

        if (!storedOtp.getExpiry().isAfter(LocalDateTime.now())) {
            return false;
        }

        if (storedOtp.getAttempts() >= MAX_ATTEMPTS) {
            otpRepository.delete(storedOtp);
            return false;
        }

        storedOtp.setAttempts(storedOtp.getAttempts() + 1);

        String codeHash = otpHashService.hash(email, otp, OtpPurpose.EMAIL_VERIFICATION.name());

        if (!codeHash.equals(storedOtp.getCode())){

            otpRepository.save(storedOtp);

            return false;
        }

        otpRepository.delete(storedOtp);

        return true;
    }

    @Override
    @Transactional
    public void clearOtp(String email) {
        otpRepository.deleteByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
    }
}
