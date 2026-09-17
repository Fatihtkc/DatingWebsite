package com.datingapp.backend.service;

import com.datingapp.backend.dto.Match.MatchDTO;
import java.util.List;

public interface MatchService {
    List<MatchDTO> getMatchesForUser(Long userId);
    boolean isMatched(Long userId1, Long userId2);
    MatchDTO createMatch(Long userId1, Long userId2);
    void deleteMatch(Long matchId);
}
