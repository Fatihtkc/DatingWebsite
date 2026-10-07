package com.datingapp.backend.dto.User;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.datingapp.backend.dto.ImageDTO;

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

    private int personalityScore;

    private List<ImageDTO> images;

}
