package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.model.Moderator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModeratorMapper {
    
    public ModeratorDTO toDTO(Moderator moderator) {
        ModeratorDTO dto = new ModeratorDTO();

        dto.setId(moderator.getId());
        dto.setFirstName(moderator.getFirstName());
        dto.setLastName(moderator.getLastName());
        dto.setEmail(moderator.getEmail());
        dto.setStartDate(moderator.getStartDate());
        dto.setRole(moderator.getRole());
        dto.setPhone(moderator.getPhone());
        dto.setImageUrl(moderator.getImageUrl());

        return dto;
    }
}
