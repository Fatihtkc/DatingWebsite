package com.datingapp.backend.mapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.model.UserLike;

@Component
@RequiredArgsConstructor
public class UserLikeMapper {

    private final UserMapper userMapper;

    public UserLikeDTO toDTO(UserLike like){

        UserLikeDTO dto = new UserLikeDTO();

        dto.setId(like.getId());
        dto.setLikedUser(userMapper.toDTO(like.getLiked()));
        dto.setLikedAt(like.getLikedAt());

        return dto;
    }

    public UserLikeDTO toDTO(UserLike like, List<UserImage> likedUserImages) {

        UserLikeDTO dto = new UserLikeDTO();

        dto.setId(like.getId());
        dto.setLikedUser(userMapper.toDTO(like.getLiked(), likedUserImages));
        dto.setLikedAt(like.getLikedAt());

        return dto;
    }
}