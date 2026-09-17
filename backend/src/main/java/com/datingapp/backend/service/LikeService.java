package com.datingapp.backend.service;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import java.util.List;

public interface LikeService {
    List<UserLikeDTO> getLikesByLiker(Long likerId);
    UserLikeDTO addLike(Long likerId, Long likedId);
    void removeLike(Long likeId);
}
