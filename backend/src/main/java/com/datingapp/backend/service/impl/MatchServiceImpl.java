package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Match;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.service.MatchService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final UserLikeRepository likeRepository;

    public MatchServiceImpl(MatchRepository matchRepository,
                            UserLikeRepository likeRepository) {
        this.matchRepository = matchRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public List<Match> getMatchesForUser(Long userId) {
        return matchRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    @Override
    public boolean isMatched(Long userId1, Long userId2) {
        return !matchRepository.findByUser1IdAndUser2Id(userId1, userId2).isEmpty() ||
               !matchRepository.findByUser1IdAndUser2Id(userId2, userId1).isEmpty();
    }

    public void deleteMatch(Long matchId) {
        matchRepository.deleteById(matchId);
    }    

    @Override
    public Match createMatch(Long userId1, Long userId2) {
        // Karşılıklı beğeni kontrolü
        UserLike like1 = likeRepository.findByLikerIdAndLikedId(userId1, userId2)
                          .orElseThrow(() -> new RuntimeException("User1 hasn't liked User2"));
        UserLike like2 = likeRepository.findByLikerIdAndLikedId(userId2, userId1)
                          .orElseThrow(() -> new RuntimeException("User2 hasn't liked User1"));
    
        // Eğer zaten match varsa, döndür
        Match existingMatch = findExistingMatch(userId1, userId2);
        if (existingMatch != null) {
            return existingMatch;
        }
    
        Match match = new Match();
        match.setUser1(like1.getLiker());
        match.setUser2(like2.getLiker());
        match.setMatchedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }
    
    private Match findExistingMatch(Long userId1, Long userId2) {
        return matchRepository.findByUser1IdAndUser2Id(userId1, userId2)
                .stream()
                .findFirst()
                .orElseGet(() -> 
                    matchRepository.findByUser1IdAndUser2Id(userId2, userId1)
                        .stream()
                        .findFirst()
                        .orElse(null)
                );
    }
    
}
