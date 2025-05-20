package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.UserImageRepository;
import com.datingapp.backend.service.UserImageService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserImageServiceImpl implements UserImageService {

    private final UserImageRepository userImageRepository;

    public UserImageServiceImpl(UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
    }

    // Kullanıcının resimlerini almak
    public List<UserImage> getImagesByUser(Long userId) {
        return userImageRepository.findByUserId(userId);
    }

    // Resmi ID ile almak
    public UserImage getImageById(Long imageId) {
        return userImageRepository.findById(imageId).orElse(null);
    }

    public List<UserImage> saveAll(List<UserImage> userImages) {
        return userImageRepository.saveAll(userImages);
    }

    @Override
    public List<UserImage> findByUserId(Long userId) {
        return userImageRepository.findByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteByUserId(Long userId) {
        userImageRepository.deleteAllByUserId(userId);
    }

}
