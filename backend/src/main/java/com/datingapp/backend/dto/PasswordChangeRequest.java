package com.datingapp.backend.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private Long id;
    private String oldPassword;
    private String newPassword;
}