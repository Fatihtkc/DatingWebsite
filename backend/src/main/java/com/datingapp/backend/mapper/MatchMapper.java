package com.datingapp.backend.mapper;

import org.springframework.stereotype.Component;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.model.Match;
import com.datingapp.backend.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final UserMapper userMapper; 

    public MatchDTO toDTO(Match match, Long currentUserId){

        User otherUser = match.getUser1().getId().equals(currentUserId)? match.getUser2(): match.getUser1();

        MatchDTO dto = new MatchDTO();
        dto.setId(match.getId());
        dto.setUser(userMapper.toDTO(otherUser));
        dto.setMatchedAt(match.getMatchedAt());

        return dto;
    }
}