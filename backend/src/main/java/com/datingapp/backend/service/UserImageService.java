package com.datingapp.backend.service;

import java.util.List;

import com.datingapp.backend.dto.ImageDTO;

public interface UserImageService {
    ImageDTO getImageById(Long imageId);
    List<ImageDTO> saveAll(Long userId, List<ImageDTO> userImages);
    List<ImageDTO> findByUserId(Long userId);
    void deleteByUserId(Long userId);

}
