package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.dto.UploadedImagesResponseDTO;
import com.datingapp.backend.enums.ModerationStatus;
import com.datingapp.backend.exception.ResourceNotFoundException;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.ImageMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.records.BatchUploadResult;
import com.datingapp.backend.records.OldImagesCleanupEvent;
import com.datingapp.backend.repository.UserImageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.UserImageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j 
@Service
@RequiredArgsConstructor
public class UserImageServiceImpl implements UserImageService {

    private final UserImageRepository userImageRepository;
    private final ImageMapper userImageMapper;
    private final UserRepository userRepository;
    private final FileStorageService storage;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public ImageDTO getImageById(Long imageId){
        UserImage userImage = userImageRepository.findByIdAndModerationStatus(imageId, ModerationStatus.APPROVED).orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));
        return userImageMapper.toDTO(userImage);
    }

    @Override
    public List<ImageDTO> findByUserId(Long userId){

        return userImageRepository.findByUserIdAndModerationStatus(userId, ModerationStatus.APPROVED).stream().map(userImageMapper::toDTO).toList();
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

        UserImage image = userImageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        image.setModerationStatus(ModerationStatus.APPROVED);
        userImageRepository.save(image);
    }

    @Override
    @Transactional
    public UploadedImagesResponseDTO replaceUserImages(Long userId, List<MultipartFile> files){

        if (files == null || files.isEmpty()){
            throw new IllegalArgumentException("At least one file must be uploaded.");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<UserImage> existingImages = userImageRepository.findByUserId(userId);

        List<String> oldFileUrls = existingImages.stream().map(UserImage::getImageUrl).filter(Objects::nonNull).toList();

        BatchUploadResult batchResult = storage.storeFiles(files);

        if (batchResult.accepted().isEmpty()){
            throw new IllegalArgumentException("No valid images were uploaded");
        }

        List<String> newlyUploaded = new ArrayList<>();

        try{

            List<UserImage> newImages = batchResult.accepted().stream()
                .map(result -> {
                    UserImage img = new UserImage();
                    img.setUser(user);
                    img.setImageUrl(result.url());
                    img.setModerationStatus(result.moderationStatus());
                    return img;
                })
                .toList();

            newlyUploaded = newImages.stream().map(UserImage::getImageUrl).filter(Objects::nonNull).toList();

            userImageRepository.deleteAllByUserId(userId);
                
            List<UserImage> saved = userImageRepository.saveAll(newImages);

            applicationEventPublisher.publishEvent(new OldImagesCleanupEvent(oldFileUrls));

            return new UploadedImagesResponseDTO(saved.stream().map(userImageMapper::toDTO).toList(), batchResult.rejected());

        } catch (Exception e){
            for (String fileUrl : newlyUploaded){
                try {
                    storage.deleteFile(fileUrl);
                } catch (Exception cleanupException){
                    log.error("Failed to cleanup uploaded file: {}", fileUrl, cleanupException);
                }
            }
            throw e;
        }

        
    }
}