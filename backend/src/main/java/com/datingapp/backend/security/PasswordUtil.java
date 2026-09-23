package com.datingapp.backend.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

    private final PasswordEncoder encoder;

    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean checkPassword(
            String rawPassword,
            String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}