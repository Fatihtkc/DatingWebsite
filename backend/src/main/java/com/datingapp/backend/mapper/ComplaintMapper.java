package com.datingapp.backend.mapper;

import org.springframework.stereotype.Component;

import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.model.Complaint;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ComplaintMapper {
    
    private final UserMapper userMapper; 
    private final ImageMapper imageMapper;

    public ComplaintDTO toDTO(Complaint complaint) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(complaint.getId());
        dto.setComplainant(userMapper.toAdminDTO(complaint.getComplainant()));
        dto.setComplained(userMapper.toAdminDTO(complaint.getComplained()));
        dto.setReason(complaint.getReason());
        dto.setComplaintDate(complaint.getComplaintDate());
        if (complaint.getImages() != null) {
            dto.setImages(
                complaint.getImages()
                    .stream()
                    .map(imageMapper::toDTO)
                    .toList()
            );
        }
        return dto;
    }
    
}
