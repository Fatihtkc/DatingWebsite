package com.datingapp.backend.controller;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.datingapp.backend.model.Token;
import com.datingapp.backend.service.EmailService;
import com.datingapp.backend.service.TokenService;

import lombok.RequiredArgsConstructor;

import com.datingapp.backend.service.OtpService;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class VerificationController {

    private final TokenService tokenService;
    private final EmailService emailService;
    private final OtpService otpService;

    // E-posta doğrulama linki gönder
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestParam String email) {
        try {
            String otp = otpService.generateOtp();
            otpService.storeOtp(email, otp);
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok("OTP sent to email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send verification email: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String code) {
        try {
            boolean isValid = otpService.verifyOtp(email, code);
            if (isValid) {
                otpService.clearOtp(email);
                return ResponseEntity.ok("OTP verified successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
    }
    
    

    // Şifre sıfırlama bağlantısı gönder
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String token = UUID.randomUUID().toString();
            tokenService.createPasswordResetToken(email, token, Duration.ofHours(1));
            emailService.sendTokenEmail(email, token, Token.TokenType.PASSWORD_RESET);
            return ResponseEntity.ok("Password reset link sent.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send reset email: " + e.getMessage());
        }
    }

    // Token geçerliliği kontrolü (frontend için)
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token, @RequestParam String type) {
        try {
            Token.TokenType tokenType = Token.TokenType.valueOf(type);
            tokenService.validateToken(token, tokenType);
            return ResponseEntity.ok("Token is valid");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token type.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }

    // Şifre sıfırlama işlemi
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            tokenService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password successfully updated.");
        } catch (Exception e) {
            e.printStackTrace();  // Log daha ayrıntılı hata detayları almanızı sağlar
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to reset password: " + e.getMessage());
        }
        
    }
    
}
