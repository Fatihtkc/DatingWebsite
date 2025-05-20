package com.datingapp.backend.service;

import com.datingapp.backend.model.Match;
import java.util.List;

public interface MatchService {
    List<Match> getMatchesForUser(Long userId);
    boolean isMatched(Long userId1, Long userId2);
    Match createMatch(Long userId1, Long userId2);
    void deleteMatch(Long matchId);
}
