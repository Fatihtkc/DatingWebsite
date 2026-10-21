/*package com.datingapp.backend.service.impl;

import com.datingapp.backend.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    @Test
    void shouldNotDeleteMatchWithoutOwnership() {
        Long matchId = 100L;
        Long attackerId = 20L;

        when(matchRepository.deleteByIdAndUserParticipation(
                matchId, attackerId
        )).thenReturn(0);

        assertThrows(
                AccessDeniedException.class,
                () -> matchService.deleteMatch(matchId, attackerId)
        );

        verify(matchRepository)
                .deleteByIdAndUserParticipation(matchId, attackerId);
    }


}*/