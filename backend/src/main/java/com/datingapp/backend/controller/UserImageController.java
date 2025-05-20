package com.datingapp.backend.controller;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.UserImageService;
import com.datingapp.backend.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users/{userId}/images")
@CrossOrigin(origins = "http://localhost:3000")
public class UserImageController {

    private final UserImageService userImageService;
    private final FileStorageService storage;
    private final UserService userService;

    public UserImageController(UserImageService userImageService, FileStorageService storage, UserService userService) {
        this.userImageService = userImageService;
        this.storage = storage;
        this.userService = userService;
    }

    // Resimleri GET ile almak
    @GetMapping
    public ResponseEntity<List<UserImage>> getImagesByUser(@PathVariable Long userId) {
        List<UserImage> userImages = userImageService.getImagesByUser(userId);
        return ResponseEntity.ok(userImages);
    }

    // Resim yüklemek (POST)
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<UserImage>> replaceUserImages(
        @PathVariable Long userId,
        @RequestPart("images") List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Kullanıcıyı al
        User user = userService.getUserById(userId);

        // 1. Mevcut resimleri sil (veritabanından ve opsiyonel olarak diskten)
        List<UserImage> existingImages = userImageService.findByUserId(userId);
        for (UserImage image : existingImages) {
            storage.deleteFile(image.getImageUrl()); // URL'den dosya adı çıkarıp silme işlemini yapmalısın
        }
        userImageService.deleteByUserId(userId);

        // 2. Yeni resimleri yükle
        List<UserImage> newImages = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(filename)
                .toUriString();

            UserImage userImage = new UserImage();
            userImage.setUser(user);
            userImage.setImageUrl(publicUrl);
            newImages.add(userImage);
        }

        List<UserImage> saved = userImageService.saveAll(newImages);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }





}
