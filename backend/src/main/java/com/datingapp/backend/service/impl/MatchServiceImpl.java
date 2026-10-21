package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.exception.ConflictException;
import com.datingapp.backend.mapper.MatchMapper;
import com.datingapp.backend.model.Match;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.repository.UserLikeRepository;
import com.datingapp.backend.service.MatchCreationService;
import com.datingapp.backend.service.MatchService;
import com.datingapp.backend.service.UserImageService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final UserLikeRepository likeRepository;
    private final MatchMapper matchMapper;
    private final UserImageService userImageService;
    private final MatchCreationService matchCreationService;


    @Override
    @Transactional(readOnly = true)
    public Page<MatchDTO> getMatchesForUser(Long userId, Pageable pageable){

        Page<Match> matches = matchRepository.findByUserId(userId, pageable);

        List<User> users = matches.getContent().stream()
                .flatMap(match -> java.util.stream.Stream.of(match.getUser1(),match.getUser2()))
                .filter(Objects::nonNull).distinct().toList();

        Map<Long, List<UserImage>> imagesByUserId = userImageService.loadApprovedImages(users);

        return matches.map(match -> matchMapper.toDTO(match, userId, imagesByUserId));
    }

    @Override
    public boolean isMatched(Long userId1, Long userId2){

        Long smallerId = Math.min(userId1, userId2);
        Long largerId = Math.max(userId1, userId2);

        return matchRepository.findByUser1IdAndUser2Id(smallerId, largerId).isPresent();
    }

    @Transactional
    @Override
    public void deleteMatch(Long matchId, Long userId){

        int deleted = matchRepository.deleteByIdAndUserParticipation(matchId, userId);

        if (deleted == 0){ 
            throw new AccessDeniedException("You cannot delete this match.");
        }
    }

    @Override
    @Transactional
    public MatchDTO createMatch(Long userId1, Long userId2){

        if(userId1 == null || userId2 == null || userId1.equals(userId2)){
            throw new IllegalArgumentException("Invalid user IDs.");
        }

        UserLike like1 = likeRepository.findByLikerIdAndLikedId(userId1, userId2).orElseThrow(() -> new ConflictException("User1 hasn't liked User2"));
        UserLike like2 = likeRepository.findByLikerIdAndLikedId(userId2, userId1).orElseThrow(() -> new ConflictException("User2 hasn't liked User1"));
    
        Match existingMatch = findExistingMatch(userId1, userId2);
        if (existingMatch != null){
            return matchMapper.toDTO(existingMatch, userId1);
        }
    
        Match match = new Match();
        if (userId1 < userId2){
            match.setUser1(like1.getLiker());
            match.setUser2(like2.getLiker());
        } else{
            match.setUser1(like2.getLiker());
            match.setUser2(like1.getLiker());
        }
        match.setMatchedAt(LocalDateTime.now());

        Match saved;
        try {
            saved = matchCreationService.createMatchRecord(match);
        } catch (DataIntegrityViolationException ex){
            Match existing = findExistingMatch(userId1, userId2);

            if (existing != null) {
                return matchMapper.toDTO(existing, userId1);
            }
            throw ex;
        }

        return matchMapper.toDTO(saved, userId1);
    }
    
    private Match findExistingMatch(Long userId1, Long userId2){

        return matchRepository.findByUser1IdAndUser2Id(userId1, userId2).or(() -> matchRepository
            .findByUser1IdAndUser2Id(userId2, userId1)).orElse(null);
    }
    
}
