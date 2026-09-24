package com.datingapp.backend.controller;

import com.datingapp.backend.dto.UserBlockDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.UserBlockService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blocks")
@RequiredArgsConstructor 
public class UserBlockController {

    private final UserBlockService blockService;
    private final UserMapper userMapper;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> blockUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody UserBlockDTO dto) {
        blockService.blockUser(principal.getId(), dto.getBlockedUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{blockedUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unblockUser(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long blockedUserId){

        blockService.unblockUser(principal.getId(), blockedUserId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getBlockedUsers(@AuthenticationPrincipal CustomUserDetails principal, @PageableDefault(page = 0, size = 20) Pageable pageable){

        Page<UserProfileDTO> blocked = blockService.getBlockedUsers(principal.getId(), pageable).map(userMapper::toDTO);

        return ResponseEntity.ok(blocked);
    }
}