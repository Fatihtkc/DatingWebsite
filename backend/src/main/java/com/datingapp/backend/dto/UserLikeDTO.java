package com.datingapp.backend.dto;

import lombok.Data;

@Data
public class UserLikeDTO {
    private Long likerId;
    private Long likedId;
}
