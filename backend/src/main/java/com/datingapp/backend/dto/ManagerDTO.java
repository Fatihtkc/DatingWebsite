package com.datingapp.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ManagerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDate hireDate;
    private String role;
    private String phone;
}
