package com.datingapp.backend.controller;

import com.datingapp.backend.dto.UserLikeDTO;
import com.datingapp.backend.model.UserLike;
import com.datingapp.backend.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/likes")
@CrossOrigin(origins = "http://localhost:3000")
public class LikeController {

    private final LikeService likeService;
    public LikeController(LikeService likeService) { this.likeService = likeService; }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<List<UserLike>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(likeService.getLikesByLiker(userId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserLike> like(@RequestBody UserLikeDTO likeRequest) {
        UserLike newLike = likeService.addLike(likeRequest.getLikerId(), likeRequest.getLikedId());
        return ResponseEntity.status(201).body(newLike);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        likeService.removeLike(id);
        return ResponseEntity.noContent().build();
    }
}
