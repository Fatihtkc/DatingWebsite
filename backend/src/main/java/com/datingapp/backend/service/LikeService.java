package com.datingapp.backend.service;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.model.UserLike;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeService {
    Page<UserLikeDTO> getLikesByLiker(Long likerId, Pageable pageable);
    UserLike addLike(Long likerId, Long likedId);
    void removeLike(Long likeId);
}
