package com.datingapp.backend.dto.Like;

import java.time.LocalDateTime;

import com.datingapp.backend.dto.User.UserProfileDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLikeDTO {
    private Long id;
    private UserProfileDTO likedUser;
    private LocalDateTime likedAt;
}
