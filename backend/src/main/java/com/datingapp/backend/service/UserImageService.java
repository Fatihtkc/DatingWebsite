package com.datingapp.backend.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.dto.UploadedImagesResponseDTO;
import com.datingapp.backend.model.UserImage;

public interface UserImageService {
    ImageDTO getImageById(Long imageId);
    List<ImageDTO> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    Page<UserImage> getPendingImages(Pageable pageable);
    void approveImage(Long imageId);
    UploadedImagesResponseDTO replaceUserImages(Long userId, List<MultipartFile> files);
}
