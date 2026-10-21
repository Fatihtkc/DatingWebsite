package com.datingapp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordResetRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 12, max = 128)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$",
        message = "The password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String newPassword;
}