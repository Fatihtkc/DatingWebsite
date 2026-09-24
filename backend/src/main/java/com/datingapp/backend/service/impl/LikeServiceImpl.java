package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.mapper.UserLikeMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.LikeService;
import com.datingapp.backend.service.NotificationService;
import com.datingapp.backend.service.UserBlockService;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final UserLikeMapper userLikeMapper;
    private final UserBlockService userBlockService;
    private final NotificationService notificationService;

    @Override
    public Page<UserLikeDTO> getLikesByLiker(Long likerId, Pageable pageable){

        return likeRepository.findByLikerId(likerId, pageable)
                .map(userLikeMapper::toDTO);
    }

    public UserLike addLike(Long likerId, Long likedId){

        if (userBlockService.isBlockedBetween(likerId, likedId)) {
            throw new AccessDeniedException("You cannot like this user.");
        }

        User liker = userRepository.findById(likerId)
                .orElseThrow(() -> new RuntimeException("Liker user not found"));
        User liked = userRepository.findById(likedId)
                .orElseThrow(() -> new RuntimeException("Liked user not found"));
        if (likeRepository.findByLikerIdAndLikedId(likerId, likedId).isPresent()) {
            throw new RuntimeException("User already liked");
        }

        UserLike like = new UserLike();
        like.setLiker(liker);
        like.setLiked(liked);
        like.setLikedAt(LocalDateTime.now());

        UserLike savedLike = likeRepository.save(like);

        notificationService.notify(liked, NotificationType.NEW_LIKE, savedLike.getId(), "New Like! 💕", "Someone likes you!", true);
        
        return savedLike;
    }

    @Override
    public void removeLike(Long likeId){

        if (!likeRepository.existsById(likeId)) {
            throw new RuntimeException("Like not found with id: " + likeId);
        }
        likeRepository.deleteById(likeId);
    }
}
