/*package com.datingapp.backend.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {


    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void shouldRejectReusingPasswordResetToken() {
        String token = "test-reset-token";
        String newPassword = "NewSecurePassword123!";

        // İlk kullanım başarılı olmalı.
        tokenService.resetPassword(token, newPassword);

        // Aynı token ikinci defa kabul edilmemeli.
        assertThrows(
                RuntimeException.class,
                () -> tokenService.resetPassword(token, newPassword)
        );
    }
}*/
