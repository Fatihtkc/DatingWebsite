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

    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerification(
            @RequestParam String email) {

        verificationService.sendVerificationCode(email);

        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String code) {

        verificationService.verifyOtp(email, code);

        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        verificationService.forgotPassword(request.getEmail());

        return ResponseEntity.ok("Password reset link sent.");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(
            @RequestParam String token,
            @RequestParam String type) {

        verificationService.validateToken(token, type);

        return ResponseEntity.ok("Token is valid.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {

        verificationService.resetPassword(token, newPassword);

        return ResponseEntity.ok("Password successfully updated.");
    }
}