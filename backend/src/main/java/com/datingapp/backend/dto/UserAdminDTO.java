package com.datingapp.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.datingapp.backend.enums.Role;

@Data
@NoArgsConstructor
public class UserAdminDTO {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

    private boolean approved;
    private boolean banned;
    private boolean confirmed;

    private Role role;
    private int personalityScore;

    private List<ImageDTO> images;

}
