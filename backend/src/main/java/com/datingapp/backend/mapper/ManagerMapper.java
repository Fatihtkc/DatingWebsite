package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.model.Manager;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerMapper {

    public ManagerDTO toDTO(Manager manager) {
        ManagerDTO dto = new ManagerDTO();

        dto.setId(manager.getId());
        dto.setFirstName(manager.getFirstName());
        dto.setLastName(manager.getLastName());
        dto.setEmail(manager.getEmail());
        dto.setStartDate(manager.getStartDate());
        dto.setRole(manager.getRole());
        dto.setPhone(manager.getPhone());
        dto.setImageUrl(manager.getImageUrl());

        return dto;
    }
    
}
