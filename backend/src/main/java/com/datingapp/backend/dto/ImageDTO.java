package com.datingapp.backend.dto;

import com.datingapp.backend.enums.ModerationStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDTO {
    private Long id;
    private String imageUrl;
    private ModerationStatus moderationStatus;
}
