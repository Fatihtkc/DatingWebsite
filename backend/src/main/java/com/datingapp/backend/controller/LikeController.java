package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Like.UserLikeDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.LikeService;
import com.datingapp.backend.mapper.UserLikeMapper;
import com.datingapp.backend.model.UserLike;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final UserLikeMapper userLikeMapper;

    @GetMapping("/mylikes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<UserLikeDTO>> listLikes(@AuthenticationPrincipal CustomUserDetails principal, @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(likeService.getLikesByLiker(principal.getId(), pageable));
    }

    @PostMapping("/users/{likedId}/like")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<UserLikeDTO> createLike(@PathVariable Long likedId, @AuthenticationPrincipal CustomUserDetails principal){

        UserLike newLike = likeService.addLike(principal.getId(), likedId);

        return ResponseEntity.status(HttpStatus.CREATED).body(userLikeMapper.toDTO(newLike));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal){

        likeService.removeLike(id, principal.getId());

        return ResponseEntity.noContent().build();
    }
}
