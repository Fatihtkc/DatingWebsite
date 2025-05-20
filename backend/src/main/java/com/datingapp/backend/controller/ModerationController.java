package com.datingapp.backend.controller;

import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.model.User;
import com.datingapp.backend.service.ModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/moderation")
@CrossOrigin(origins = "http://localhost:3000")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    // 1. Bekleyen profilleri listele
    @GetMapping("/pending-users")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(moderationService.listPendingUsers());
    }

    // 2. Profili onayla
    @PutMapping("/approve-user/{userId}")
    public ResponseEntity<User> approveUser(@PathVariable Long userId) {
        return ResponseEntity.ok(moderationService.approveUser(userId));
    }

    // 3. Kullanıcıyı banla
    @PutMapping("/ban-user/{userId}")
    public ResponseEntity<User> banUser(@PathVariable Long userId) {
        return ResponseEntity.ok(moderationService.banUser(userId));
    }

    // 4. Tüm şikayetleri listele
    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(moderationService.listAllComplaints());
    }

    // 5. Şikayet detayı
    @GetMapping("/complaints/{complaintId}")
    public ResponseEntity<Complaint> getComplaint(@PathVariable Long complaintId) {
        return ResponseEntity.ok(moderationService.getComplaint(complaintId));
    }

    // 6. Şikayeti sil
    @DeleteMapping("/complaints/{complaintId}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long complaintId) {
        moderationService.deleteComplaint(complaintId);
        return ResponseEntity.noContent().build();
    }
}
