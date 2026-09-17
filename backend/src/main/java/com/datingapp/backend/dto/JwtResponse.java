package com.datingapp.backend.dto;

import com.datingapp.backend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor 
public class JwtResponse {
    private Long id;
    private String username;
    private String token;
    private Role role;
}