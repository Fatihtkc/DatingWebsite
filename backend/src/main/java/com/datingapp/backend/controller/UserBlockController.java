package com.datingapp.backend.controller;

import com.datingapp.backend.dto.User.UserBlockDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.UserBlockService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor 
public class UserBlockController {

    private final UserBlockService blockService;

    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<Void> blockUser(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody @Valid UserBlockDTO dto){

        blockService.blockUser(principal.getId(), dto.getBlockedUserId());
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{blockedUserId}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<Void> unblockUser(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long blockedUserId){

        blockService.unblockUser(principal.getId(), blockedUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<Page<UserProfileDTO>> getBlockedUsers(@AuthenticationPrincipal CustomUserDetails principal, @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(blockService.getBlockedUsers(principal.getId(), pageable));
    }
}