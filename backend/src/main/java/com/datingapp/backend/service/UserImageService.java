package com.datingapp.backend.service;

import com.datingapp.backend.model.UserImage;
import java.util.List;

public interface UserImageService {
    // Bir kullanıcıya ait tüm resimleri getir
    List<UserImage> getImagesByUser(Long userId);

    UserImage getImageById(Long imageId);
    List<UserImage> saveAll(List<UserImage> userImages);
    List<UserImage> findByUserId(Long userId);
    void deleteByUserId(Long userId);

}
