package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService compService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR','MANAGER')")
    public ResponseEntity<List<ComplaintDTO>> listAllComplaints() {
        return ResponseEntity.ok(compService.getAllComplaints());
    }

    // id ile şikayet getir
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','MANAGER')")
    public ResponseEntity<ComplaintDTO> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(compService.getComplaint(id));
    }

    // Yeni şikayet oluştur
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ComplaintDTO> createComplaint(@RequestBody @Valid ComplaintCreateDTO dto,@AuthenticationPrincipal CustomUserDetails principal) {

        Long complainantId = principal.getId();
        ComplaintDTO createdComplaint = compService.createComplaint(complainantId,dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComplaint);
    }
    
    // Şikayeti sil
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        compService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
