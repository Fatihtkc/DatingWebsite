package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.mapper.MatchMapper;
import com.datingapp.backend.model.Match;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.service.MatchService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final UserLikeRepository likeRepository;
    private final MatchMapper matchMapper;

    @Override
    public List<MatchDTO> getMatchesForUser(Long userId) {
        return matchRepository.findByUserId(userId).stream()
                .map(matchMapper::toDTO)
                .toList();
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
    public MatchDTO createMatch(Long userId1, Long userId2) {
        UserLike like1 = likeRepository.findByLikerIdAndLikedId(userId1, userId2)
                          .orElseThrow(() -> new RuntimeException("User1 hasn't liked User2"));
        UserLike like2 = likeRepository.findByLikerIdAndLikedId(userId2, userId1)
                          .orElseThrow(() -> new RuntimeException("User2 hasn't liked User1"));
    
        Match existingMatch = findExistingMatch(userId1, userId2);
        if (existingMatch != null) {
            return matchMapper.toDTO(existingMatch);
        }
    
        Match match = new Match();
        if (userId1 < userId2) {
            match.setUser1(like1.getLiker());
            match.setUser2(like2.getLiker());
        } else {
            match.setUser1(like2.getLiker());
            match.setUser2(like1.getLiker());
        }
        match.setMatchedAt(LocalDateTime.now());
        
        return matchMapper.toDTO(matchRepository.save(match));
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
