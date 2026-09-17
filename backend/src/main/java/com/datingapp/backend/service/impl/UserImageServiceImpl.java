package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.mapper.ImageMapper;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.UserImageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserImageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserImageServiceImpl implements UserImageService {

    private final UserImageRepository userImageRepository;
    private final ImageMapper userImageMapper;
    private final UserRepository userRepository;

    // Resmi ID ile almak
    public ImageDTO getImageById(Long imageId) {
        UserImage userImage = userImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
        return userImageMapper.toDTO(userImage);
    }

    public List<ImageDTO> saveAll(Long userId, List<ImageDTO> userImages){
        
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<UserImage> entities = userImages.stream()
                .map(dto -> {
                    UserImage image = userImageMapper.toUserImageEntity(dto);
                    image.setUser(user);
                    return image;
                })
                .toList();

        return userImageRepository.saveAll(entities).stream()
                .map(userImageMapper::toDTO)
                .toList();
    }

    @Override
    public List<ImageDTO> findByUserId(Long userId) {
        return userImageRepository.findByUserId(userId).stream()
                .map(userImageMapper::toDTO)
                .toList();
    }

    @Transactional
    @Override
    public void deleteByUserId(Long userId) {
        userImageRepository.deleteAllByUserId(userId);
    }

}
