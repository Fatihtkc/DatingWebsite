package com.datingapp.backend.controller;

import com.datingapp.backend.dto.ForgotPasswordRequest;
import com.datingapp.backend.dto.PasswordResetRequest;
import com.datingapp.backend.service.VerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<String> sendVerification(@RequestParam String email){

        verificationService.sendVerificationCode(email);

        return ResponseEntity.ok("OTP sent to email.");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp( @RequestParam String email,@RequestParam String otp){

        verificationService.verifyOtp(email, otp);

        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){

        verificationService.forgotPassword(request.getEmail());

        return ResponseEntity.ok("If the account exists, a password reset email has been sent.");
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token, @RequestParam String type){

        verificationService.validateToken(token, type);

        return ResponseEntity.ok("Token is valid.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody PasswordResetRequest request){

        verificationService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok("Password successfully updated.");
    }
}