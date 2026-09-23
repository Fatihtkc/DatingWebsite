package com.datingapp.backend.controller;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;

import com.datingapp.backend.service.ModerationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/moderation")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;

    // 1. Bekleyen profilleri listele
    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<UserAdminDTO>> getPendingUsers() {
        return ResponseEntity.ok(moderationService.listPendingUsers());
    }

    // 2. Profili onayla
    @PutMapping("/approve-user/{userId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<UserAdminDTO> approveUser(@PathVariable Long userId) {
        return ResponseEntity.ok(moderationService.approveUser(userId));
    }

    // 3. Kullanıcıyı banla
    @PutMapping("/ban-user/{userId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<UserAdminDTO> banUser(@PathVariable Long userId) {
        return ResponseEntity.ok(moderationService.banUser(userId));
    }

    // 4. Tüm şikayetleri listele
    @GetMapping("/complaints")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<List<ComplaintDTO>> getAllComplaints() {
        return ResponseEntity.ok(moderationService.listAllComplaints());
    }

    // 5. Şikayet detayı
    @GetMapping("/complaints/{complaintId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ComplaintDTO> getComplaint(@PathVariable Long complaintId) {
        return ResponseEntity.ok(moderationService.getComplaint(complaintId));
    }

    // 6. Şikayeti sil
    @DeleteMapping("/complaints/{complaintId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long complaintId) {
        moderationService.deleteComplaint(complaintId);
        return ResponseEntity.noContent().build();
    }
}
