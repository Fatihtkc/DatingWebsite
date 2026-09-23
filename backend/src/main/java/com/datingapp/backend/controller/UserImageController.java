package com.datingapp.backend.controller;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.UserImageService;
import com.datingapp.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserImageController {

    private final UserImageService userImageService;
    private final FileStorageService storage;

    @GetMapping("/me/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ImageDTO>> getMyImages(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getId();
        return ResponseEntity.ok(userImageService.findByUserId(userId));
    }

    // Resimleri GET ile almak
    @GetMapping("/{userId}/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ImageDTO>> getImagesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userImageService.findByUserId(userId));
    }

    // Resim yüklemek (POST)
    @PostMapping(value = "/me/images", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<List<ImageDTO>> replaceUserImages(@AuthenticationPrincipal CustomUserDetails principal, @RequestPart("images") List<MultipartFile> files) {

        Long userId = principal.getId();

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // 1. Mevcut resimleri sil (veritabanından ve opsiyonel olarak diskten)
        List<ImageDTO> existingImages = userImageService.findByUserId(userId);
        for (ImageDTO image : existingImages) {
            storage.deleteFile(image.getImageUrl()); // URL'den dosya adı çıkarıp silme işlemini yapmalısın
        }
        userImageService.deleteByUserId(userId);

        // 2. Yeni resimleri yükle
        List<ImageDTO> newImages = new ArrayList<>();

        for (MultipartFile file : files) {
            String filename = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(filename)
                .toUriString();

            ImageDTO dto = new ImageDTO();

            dto.setImageUrl(publicUrl);
            newImages.add(dto);
        }

        List<ImageDTO> saved = userImageService.saveAll(userId, newImages);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }





}
