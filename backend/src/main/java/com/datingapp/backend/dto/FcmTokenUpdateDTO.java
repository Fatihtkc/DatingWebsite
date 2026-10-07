package com.datingapp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
public class FcmTokenUpdateDTO{

    @NotBlank
    private String token;
}