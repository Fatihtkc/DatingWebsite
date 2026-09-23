package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Like.LikeRequestDTO;
import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.LikeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<List<UserLikeDTO>> listLikes(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getId();
        return ResponseEntity.ok(likeService.getLikesByLiker(userId));
    }

    @PostMapping("/users/{likedId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserLikeDTO> Createlike(@PathVariable Long likedId,
        @AuthenticationPrincipal CustomUserDetails principal) {
        UserLikeDTO newLike = likeService.addLike(principal.getId(), likedId);
        return ResponseEntity.status(201).body(newLike);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        likeService.removeLike(id);
        return ResponseEntity.noContent().build();
    }
}
