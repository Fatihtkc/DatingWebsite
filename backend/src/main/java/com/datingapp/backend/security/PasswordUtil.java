package com.datingapp.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();

    // Şifreyi şifrelemek
    public static String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // Şifreyi doğrulamak
    public static boolean checkPassword(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
