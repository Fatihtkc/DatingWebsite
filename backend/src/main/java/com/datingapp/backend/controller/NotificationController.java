package com.datingapp.backend.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.datingapp.backend.dto.NotificationDTO;
import com.datingapp.backend.dto.NotificationSettingsDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @PutMapping("/fcm-token")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateFcmToken(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody Map<String, String> body){

        notificationService.updateFcmToken(principal.getId(), body.get("token"));

        return ResponseEntity.ok().build();
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NotificationDTO>> getNotifications(@AuthenticationPrincipal CustomUserDetails principal, Pageable pageable){

        return ResponseEntity.ok(notificationService.getNotifications(principal.getId(),pageable));
    }


    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal CustomUserDetails principal){

        return ResponseEntity.ok(notificationService.getUnreadCount(principal.getId()));
    }


    @PutMapping("/mark-all-read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal CustomUserDetails principal){

        notificationService.markAllAsRead(principal.getId());

        return ResponseEntity.ok().build();
    }


    @PutMapping("/settings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateSettings(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody NotificationSettingsDTO dto){

        notificationService.updateSettings(principal.getId(),dto);

        return ResponseEntity.ok().build();
    }
}