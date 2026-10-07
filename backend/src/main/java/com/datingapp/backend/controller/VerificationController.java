package com.datingapp.backend.controller;

import com.datingapp.backend.dto.ForgotPasswordRequest;
import com.datingapp.backend.service.VerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/api/auth/send-verification")
    public ResponseEntity<String> sendVerification(@RequestParam String email){

        verificationService.sendVerificationCode(email);

        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/api/auth/verify")
    public ResponseEntity<String> verifyOtp( @RequestParam String email,@RequestParam String otp){

        verificationService.verifyOtp(email, otp);

        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/api/auth/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        verificationService.forgotPassword(request.getEmail());

        return ResponseEntity.ok("If the account exists, a password reset email has been sent.");
    }

    @GetMapping("/api/auth/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token, @RequestParam String type){

        verificationService.validateToken(token, type);

        return ResponseEntity.ok("Token is valid.");
    }

    @PostMapping("/api/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword){

        verificationService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Password successfully updated.");
    }
}