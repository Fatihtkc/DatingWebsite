package com.datingapp.backend.controller;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.service.ModerationService;
import com.datingapp.backend.service.UserImageService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderation")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ModerationController {

    private final ModerationService moderationService;
    private final UserImageService userImageService;

    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Page<UserAdminDTO>> getPendingUsers(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(moderationService.listPendingUsers(pageable));
    }

    @PutMapping("/approve-user/{userId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<UserAdminDTO> approveUser(@PathVariable Long userId){

        return ResponseEntity.ok(moderationService.approveUser(userId));
    }

    @PutMapping("/ban-user/{userId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<UserAdminDTO> banUser(@PathVariable Long userId){

        return ResponseEntity.ok(moderationService.banUser(userId));
    }

    @GetMapping("/complaints")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Page<ComplaintDTO>> getAllComplaints(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(moderationService.listAllComplaints(pageable));
    }

    @GetMapping("/complaints/{complaintId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ComplaintDTO> getComplaint(@PathVariable Long complaintId){

        return ResponseEntity.ok(moderationService.getComplaint(complaintId));
    }

    @DeleteMapping("/complaints/{complaintId}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long complaintId){

        moderationService.deleteComplaint(complaintId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/moderation/pending-images")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('MANAGER')")
    public ResponseEntity<Page<UserImage>> getPendingImages(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userImageService.getPendingImages(pageable));
    }

    @PutMapping("/moderation/images/{imageId}/approve")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('MANAGER')")
    public ResponseEntity<Void> approveImage(@PathVariable Long imageId){

        userImageService.approveImage(imageId);

        return ResponseEntity.noContent().build();
    }
}
