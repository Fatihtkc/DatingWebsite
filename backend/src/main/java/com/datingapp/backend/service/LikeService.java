package com.datingapp.backend.service;

import com.datingapp.backend.model.UserLike;
import java.util.List;

public interface LikeService {
    List<UserLike> getLikesByLiker(Long likerId);
    UserLike addLike(Long likerId, Long likedId);
    void removeLike(Long likeId);
}
