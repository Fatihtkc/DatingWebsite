package com.datingapp.backend.mapper;

import org.springframework.stereotype.Component;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.model.Match;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final UserMapper userMapper; 

    public MatchDTO toDTO(Match match) {

        MatchDTO dto = new MatchDTO();

        dto.setId(match.getId());
        dto.setUser(userMapper.toDTO(match.getUser2()));
        dto.setMatchedAt(match.getMatchedAt());

        return dto;
    }
}