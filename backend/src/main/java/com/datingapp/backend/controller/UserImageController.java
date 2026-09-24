package com.datingapp.backend.controller;

import com.datingapp.backend.dto.ImageDTO;
import com.datingapp.backend.dto.UploadedImagesResponseDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.UserImageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserImageController {

    private final UserImageService userImageService;

    @GetMapping("/me/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ImageDTO>> getMyImages(@AuthenticationPrincipal CustomUserDetails principal){
        return ResponseEntity.ok(userImageService.findByUserId(principal.getId()));
    }

    @GetMapping("/{userId}/images")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ImageDTO>> getImagesByUser(@PathVariable Long userId){
        return ResponseEntity.ok(userImageService.findByUserId(userId));
    }

    @PostMapping(value = "/me/images", consumes = "multipart/form-data")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<UploadedImagesResponseDTO> replaceUserImages(@AuthenticationPrincipal CustomUserDetails principal, @RequestPart("images") List<MultipartFile> files){

        UploadedImagesResponseDTO response = userImageService.replaceUserImages(principal.getId(), files);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}