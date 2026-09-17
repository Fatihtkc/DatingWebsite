package com.datingapp.backend.dto.Match;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.datingapp.backend.dto.User.UserProfileDTO;

@Data
@NoArgsConstructor
public class MatchDTO {
    private Long id;
    private UserProfileDTO user;
    private LocalDateTime matchedAt;
}
