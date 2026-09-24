package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.dto.UploadedImagesResponseDTO;
import com.datingapp.backend.enums.ModerationStatus;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.ImageMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.UserImageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.UserImageService;
import com.datingapp.backend.service.records.BatchUploadResult;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserImageServiceImpl implements UserImageService {

    private final UserImageRepository userImageRepository;
    private final ImageMapper userImageMapper;
    private final UserRepository userRepository;
    private final FileStorageService storage;

    @Override
    public ImageDTO getImageById(Long imageId){
        UserImage userImage = userImageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        return userImageMapper.toDTO(userImage);
    }

    @Override
    public List<ImageDTO> findByUserId(Long userId){

        return userImageRepository.findByUserId(userId).stream().map(userImageMapper::toDTO).toList();
    }

    @Transactional
    @Override
    public void deleteByUserId(Long userId){

        userImageRepository.deleteAllByUserId(userId);
    }

    @Override
    public Page<UserImage> getPendingImages(Pageable pageable){

        return userImageRepository.findByModerationStatus(ModerationStatus.PENDING, pageable);
    }

    @Override
    @Transactional
    public void approveImage(Long imageId){

        UserImage image = userImageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found"));
        image.setModerationStatus(ModerationStatus.APPROVED);
        userImageRepository.save(image);
    }

    @Override
    @Transactional
    public UploadedImagesResponseDTO replaceUserImages(Long userId, List<MultipartFile> files){
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("En az bir dosya yüklemelisiniz.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<UserImage> existingImages = userImageRepository.findByUserId(userId);
        for (UserImage image : existingImages) {
            storage.deleteFile(image.getImageUrl());
        }
        userImageRepository.deleteAllByUserId(userId);

        BatchUploadResult batchResult = storage.storeFiles(files);

        List<UserImage> newImages = batchResult.accepted().stream()
            .map(result -> {
                UserImage img = new UserImage();
                img.setUser(user);
                img.setImageUrl(result.url());
                img.setModerationStatus(result.moderationStatus());
                return img;
            })
            .toList();

        List<UserImage> saved = userImageRepository.saveAll(newImages);

        return new UploadedImagesResponseDTO(
            saved.stream().map(userImageMapper::toDTO).toList(),
            batchResult.rejected()
        );
    }
}