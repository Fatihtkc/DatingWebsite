package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserLike;
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

    @Override
    public List<UserLike> getLikesByLiker(Long likerId) {
        return likeRepository.findByLikerId(likerId);
    }

    public UserLike addLike(Long likerId, Long likedId) {
        User liker = userRepository.findById(likerId)
                .orElseThrow(() -> new RuntimeException("Liker user not found"));
        User liked = userRepository.findById(likedId)
                .orElseThrow(() -> new RuntimeException("Liked user not found"));
        // Kullanıcıları konsola yazdırarak kontrol et
    System.out.println("Liker: " + liker);
    System.out.println("Liked: " + liked);

        UserLike like = new UserLike();
        like.setLiker(liker);
        like.setLiked(liked);
        like.setLikedAt(LocalDateTime.now());

        return likeRepository.save(like);
    }

    @Override
    public void removeLike(Long likeId) {
        if (!likeRepository.existsById(likeId)) {
            throw new RuntimeException("Like not found with id: " + likeId);
        }
        likeRepository.deleteById(likeId);
    }
}
