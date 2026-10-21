package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Complaint.ComplaintCreateDTO;
import com.datingapp.backend.dto.Complaint.ComplaintDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService compService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR','MANAGER')")
    public ResponseEntity<Page<ComplaintDTO>> listAllComplaints(@PageableDefault(page = 0, size = 20) Pageable pageable){
        return ResponseEntity.ok(compService.getAllComplaints(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','MANAGER')")
    public ResponseEntity<ComplaintDTO> getComplaintById(@PathVariable Long id){
        return ResponseEntity.ok(compService.getComplaint(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    public ResponseEntity<ComplaintDTO> createComplaint(@RequestPart("data") @Valid ComplaintCreateDTO dto, 
        @AuthenticationPrincipal CustomUserDetails principal, 
        @RequestPart(value = "images", required = false) List<MultipartFile> files){

        ComplaintDTO createdComplaint = compService.createComplaint(principal.getId(), dto, files);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComplaint);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id){
        
        compService.deleteComplaint(id);

        return ResponseEntity.noContent().build();
    }
}
