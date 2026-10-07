package com.datingapp.backend.dto.Complaint;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ComplaintCreateDTO{

    @NotNull
    private Long complainedId; 

    @NotBlank(message = "Reason for complaint cannot be empty")
    @Size(min = 10, max = 1000)
    private String reason;
    
}
