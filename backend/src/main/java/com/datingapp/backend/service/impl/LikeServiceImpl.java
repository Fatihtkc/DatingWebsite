package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.mapper.MatchMapper;
import com.datingapp.backend.mapper.UserLikeMapper;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.LikeService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final UserLikeRepository likeRepository;
    private final UserRepository userRepository;
    private final UserLikeMapper userLikeMapper;

    @Override
    public List<UserLikeDTO> getLikesByLiker(Long likerId) {
        return likeRepository.findByLikerId(likerId)
                .stream()
                .map(userLikeMapper::toDTO)
                .toList();
    }

    public UserLikeDTO addLike(Long likerId, Long likedId) {

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
        
        return userLikeMapper.toDTO(savedLike);
    }

    @Override
    public void removeLike(Long likeId) {
        if (!likeRepository.existsById(likeId)) {
            throw new RuntimeException("Like not found with id: " + likeId);
        }
        likeRepository.deleteById(likeId);
    }
}
