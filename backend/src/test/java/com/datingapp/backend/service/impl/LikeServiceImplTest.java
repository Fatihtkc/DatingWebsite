/*package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.UserLikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private UserLikeRepository userLikeRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    void shouldRejectDeletingAnotherUsersLike() {
        // Arrange
        Long likeId = 100L;
        Long attackerId = 20L;

        when(userLikeRepository.findByIdAndLikerId(likeId, attackerId))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                AccessDeniedException.class,
                () -> likeService.removeLike(likeId, attackerId)
        );

        verify(userLikeRepository, never()).delete(any());
    }

    @Test
    void shouldAllowDeletingOwnLike() {
        Long likeId = 100L;
        Long userId = 20L;

        UserLike like = mock(UserLike.class);

        when(userLikeRepository.findByIdAndLikerId(likeId, userId))
                .thenReturn(Optional.of(like));

        likeService.removeLike(likeId, userId);

        verify(userLikeRepository).delete(like);
    }
}*/