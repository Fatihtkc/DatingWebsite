package com.datingapp.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordChangeRequest {
    private Long id;
    private String oldPassword;
    private String newPassword;
}