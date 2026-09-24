package com.datingapp.backend.service;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.model.Match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchService {

    Page<MatchDTO> getMatchesForUser(Long userId, Pageable pageable);
    boolean isMatched(Long userId1, Long userId2);
    Match createMatch(Long userId1, Long userId2);
    void deleteMatch(Long matchId);
    
}
