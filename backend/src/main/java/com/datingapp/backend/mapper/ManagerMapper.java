package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.Manager.ManagerCreateDTO;
import com.datingapp.backend.dto.Manager.ManagerUpdateDTO;
import com.datingapp.backend.model.Manager;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerMapper {

    public ManagerUpdateDTO toDTO(Manager manager) {
        ManagerUpdateDTO dto = new ManagerUpdateDTO();

        dto.setId(manager.getId());
        dto.setFirstName(manager.getFirstName());
        dto.setLastName(manager.getLastName());
        dto.setEmail(manager.getEmail());
        dto.setStartDate(manager.getStartDate());
        dto.setPhone(manager.getPhone());
        dto.setImageUrl(manager.getImageUrl());

        return dto;
    }

    public Manager toEntity(ManagerCreateDTO dto){

        Manager manager = new Manager();

        manager.setFirstName(dto.getFirstName());
        manager.setLastName(dto.getLastName());
        manager.setEmail(dto.getEmail());
        manager.setBirthDate(dto.getBirthDate());
        manager.setStartDate(dto.getStartDate());
        manager.setPhone(dto.getPhone());
        manager.setImageUrl(dto.getImageUrl());

        return manager;
    }

    public void updateEntity(ManagerUpdateDTO dto, Manager manager){

        manager.setFirstName(dto.getFirstName());
        manager.setLastName(dto.getLastName());
        manager.setEmail(dto.getEmail());
        manager.setBirthDate(dto.getBirthDate());
        manager.setStartDate(dto.getStartDate());
        manager.setPhone(dto.getPhone());
        manager.setImageUrl(dto.getImageUrl());
        
    }
}
