package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.mapper.MatchMapper;
import com.datingapp.backend.model.Match;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.service.MatchService;
import com.datingapp.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final UserLikeRepository likeRepository;
    private final MatchMapper matchMapper;
    private final NotificationService notificationService;

    @Override
    public Page<MatchDTO> getMatchesForUser(Long userId, Pageable pageable){

        return matchRepository.findByUserId(userId, pageable).map(matchMapper::toDTO);
    }

    @Override
    public boolean isMatched(Long userId1, Long userId2){

        return !matchRepository.findByUser1IdAndUser2Id(userId1, userId2).isEmpty() ||
               !matchRepository.findByUser1IdAndUser2Id(userId2, userId1).isEmpty();
    }

    public void deleteMatch(Long matchId){
        matchRepository.deleteById(matchId);
    }    

    @Override
    public Match createMatch(Long userId1, Long userId2){
        UserLike like1 = likeRepository.findByLikerIdAndLikedId(userId1, userId2)
                          .orElseThrow(() -> new RuntimeException("User1 hasn't liked User2"));
        UserLike like2 = likeRepository.findByLikerIdAndLikedId(userId2, userId1)
                          .orElseThrow(() -> new RuntimeException("User2 hasn't liked User1"));
    
        Match existingMatch = findExistingMatch(userId1, userId2);
        if (existingMatch != null) {
            return existingMatch;
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

        Match saved = matchRepository.save(match);

        notificationService.notify(saved.getUser1(), NotificationType.NEW_MATCH, saved.getId(),
            "New Match! 🎉", "You matched with " + saved.getUser2().getFirstName() + " " + saved.getUser2().getLastName() + "!", true);
        notificationService.notify(saved.getUser2(), NotificationType.NEW_MATCH, saved.getId(),
            "New Match! 🎉", "You matched with " + saved.getUser1().getFirstName() + " " + saved.getUser1().getLastName() + "!", true);
        
        return saved;
    }
    
    private Match findExistingMatch(Long userId1, Long userId2){

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
