package com.datingapp.backend.controller;

import com.datingapp.backend.model.Complaint;
import com.datingapp.backend.service.ComplaintService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
@CrossOrigin(origins = "http://localhost:3000")
public class ComplaintController {

    private final ComplaintService compService;

    public ComplaintController(ComplaintService compService) {
        this.compService = compService;
    }

    // Tüm şikayetleri getir
    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR','MANAGER')")
    public ResponseEntity<List<Complaint>> listAllComplaints() {
        return ResponseEntity.ok(compService.getAllComplaints());
    }

    // id ile şikayet getir
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','MANAGER')")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        return ResponseEntity.ok(compService.getComplaint(id));
    }

    // Yeni şikayet oluştur
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Complaint> createComplaint(@RequestBody @Valid Complaint complaint) {
        Complaint createdComplaint = compService.createComplaint(complaint);
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
