package com.datingapp.backend.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.dto.User.UserAdminDTO;
import com.datingapp.backend.projection.ComplaintProjection;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ComplaintMapper {

    public ComplaintDTO toDTO(ComplaintProjection projection){
        ComplaintDTO dto = new ComplaintDTO();

        dto.setId(projection.getId());
        dto.setReason(projection.getReason());
        dto.setComplaintDate(projection.getComplaintDate());

        UserAdminDTO complainant = new UserAdminDTO();

        complainant.setId(projection.getComplainantId());
        complainant.setUsername(projection.getComplainantUsername());
        complainant.setFirstName(projection.getComplainantFirstName());
        complainant.setLastName(projection.getComplainantLastName());
        complainant.setEmail(projection.getComplainantEmail());

        if (projection.getComplainantImageUrl() != null){
            ImageDTO image = new ImageDTO();
            image.setImageUrl(projection.getComplainantImageUrl());
            complainant.setImages(List.of(image));
        }

        dto.setComplainant(complainant);

        UserAdminDTO complained = new UserAdminDTO();

        complained.setId(projection.getComplainedId());
        complained.setUsername(projection.getComplainedUsername());
        complained.setFirstName(projection.getComplainedFirstName());
        complained.setLastName(projection.getComplainedLastName());
        complained.setEmail(projection.getComplainedEmail());

        if (projection.getComplainedImageUrl() != null){
            ImageDTO image = new ImageDTO();
            image.setImageUrl(projection.getComplainedImageUrl());
            complained.setImages(List.of(image));
        }

        dto.setComplained(complained);

        return dto;
    }
}
