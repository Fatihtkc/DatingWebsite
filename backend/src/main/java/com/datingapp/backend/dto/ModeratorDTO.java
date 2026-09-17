package com.datingapp.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import com.datingapp.backend.enums.Role;

@Data
@NoArgsConstructor
public class ModeratorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private LocalDate startDate;
    private String phone;
    private Role role;
    private String ImageUrl;
}
