package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.exception.ConflictException;
import com.datingapp.backend.exception.ResourceNotFoundException;
import com.datingapp.backend.mapper.UserLikeMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.LikeService;
import com.datingapp.backend.service.NotificationService;
import com.datingapp.backend.service.UserBlockService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

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

    @Override
    @Transactional 
    public UserLike addLike(Long likerId, Long likedId){

        if (userBlockService.isBlockedBetween(likerId, likedId)) {
            throw new AccessDeniedException("You cannot like this user.");
        }

        User liker = userRepository.findById(likerId)
                .orElseThrow(() -> new ResourceNotFoundException("Liker user not found"));
        User liked = userRepository.findById(likedId)
                .orElseThrow(() -> new ResourceNotFoundException("Liked user not found"));
        if (likeRepository.findByLikerIdAndLikedId(likerId, likedId).isPresent()) {
            throw new ConflictException("User already liked");
        }

        UserLike like = new UserLike();
        like.setLiker(liker);
        like.setLiked(liked);
        like.setLikedAt(LocalDateTime.now());

        try {
            UserLike savedLike = likeRepository.save(like);

            notificationService.notify(liked, NotificationType.NEW_LIKE, savedLike.getId(), "New Like! 💕", "Someone likes you!", true);
         
            return savedLike;

        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("User already liked");
        }
    }

    @Override
    @Transactional 
    public void removeLike(Long likeId, Long likerId){

        UserLike like = likeRepository.findByIdAndLikerId(likeId, likerId).orElseThrow(() -> new AccessDeniedException("You cannot delete this like."));

        likeRepository.delete(like);
    }
}
