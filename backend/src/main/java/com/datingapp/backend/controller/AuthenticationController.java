package com.datingapp.backend.controller;

import com.datingapp.backend.service.LoginService;
import com.datingapp.backend.service.RefreshTokenService;
import com.datingapp.backend.dto.JwtResponseDTO;
import com.datingapp.backend.dto.LoginRequestDTO;
import com.datingapp.backend.dto.RefreshRequestDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController{

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request){

        String ip = resolveIp(request);

        JwtResponseDTO response = loginService.login(loginRequest, ip);

        return ResponseEntity.ok(response);
    }

    private String resolveIp(HttpServletRequest request){
        
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()){
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

     @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO req) {
        var t = refreshTokenService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(new JwtResponseDTO(
                t.id(), t.username(), t.accessToken(), t.refreshToken(), t.role()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDTO req) {
        refreshTokenService.revoke(req.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}