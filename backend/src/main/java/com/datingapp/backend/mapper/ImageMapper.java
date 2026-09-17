package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.model.ComplaintImage;
import com.datingapp.backend.model.UserImage;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageMapper {

    public UserImage toUserImageEntity(ImageDTO dto) {
        UserImage image = new UserImage();

        image.setId(dto.getId());
        image.setImageUrl(dto.getImageUrl());

        return image;
    }

    public ComplaintImage toComplaintImageEntity(ImageDTO dto) {
        ComplaintImage image = new ComplaintImage();

        image.setId(dto.getId());
        image.setImageUrl(dto.getImageUrl());

        return image;
    }

    public ImageDTO toDTO(UserImage image) {
        ImageDTO dto = new ImageDTO();

        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());

        return dto;
    }

    public ImageDTO toDTO(ComplaintImage image) {
        ImageDTO dto = new ImageDTO();

        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());

        return dto;
    }
}
