package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.Moderator.ModeratorCreateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorUpdateDTO;
import com.datingapp.backend.model.Moderator;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModeratorMapper {
    
    public ModeratorUpdateDTO toDTO(Moderator moderator){

        ModeratorUpdateDTO dto = new ModeratorUpdateDTO();

        dto.setId(moderator.getId());
        dto.setFirstName(moderator.getFirstName());
        dto.setLastName(moderator.getLastName());
        dto.setEmail(moderator.getEmail());
        dto.setBirthDate(moderator.getBirthDate());
        dto.setStartDate(moderator.getStartDate());
        dto.setPhone(moderator.getPhone());
        dto.setImageUrl(moderator.getImageUrl());

        return dto;
    }

    public Moderator toEntity(ModeratorCreateDTO dto){

        Moderator moderator = new Moderator();

        moderator.setFirstName(dto.getFirstName());
        moderator.setLastName(dto.getLastName());
        moderator.setEmail(dto.getEmail());
        moderator.setBirthDate(dto.getBirthDate());
        moderator.setStartDate(dto.getStartDate());
        moderator.setPhone(dto.getPhone());
        moderator.setImageUrl(dto.getImageUrl());

        return moderator;
    }

    public void updateEntity(ModeratorUpdateDTO dto, Moderator moderator){

        moderator.setFirstName(dto.getFirstName());
        moderator.setLastName(dto.getLastName());
        moderator.setEmail(dto.getEmail());
        moderator.setBirthDate(dto.getBirthDate());
        moderator.setStartDate(dto.getStartDate());
        moderator.setPhone(dto.getPhone());
        moderator.setImageUrl(dto.getImageUrl());
        
    }

}
