package com.datingapp.backend.controller;

import com.datingapp.backend.dto.PasswordChangeRequestDTO;
import com.datingapp.backend.dto.Manager.ManagerCreateDTO;
import com.datingapp.backend.dto.Manager.ManagerUpdateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorCreateDTO;
import com.datingapp.backend.dto.Moderator.ModeratorUpdateDTO;
import com.datingapp.backend.dto.User.UserAdminDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ManagerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;    

    @GetMapping("/moderators")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<ModeratorUpdateDTO>> getAllModerators(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(managerService.listAllModerators(pageable));
    }

    @GetMapping("/moderator/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('MODERATOR')")
    public ResponseEntity<ModeratorUpdateDTO> getModeratorById(@PathVariable Long id) {

        return ResponseEntity.ok(managerService.getModeratorById(id));
    }

    @PostMapping(value = "/moderators", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ModeratorUpdateDTO> hireModerator(@RequestPart("data") @Valid ModeratorCreateDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.status(HttpStatus.CREATED).body(managerService.hireModerator(dto, file));
    }

    @PutMapping(value = "/moderators/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ModeratorUpdateDTO> updateModerator(@PathVariable Long id, @RequestPart("data") @Valid ModeratorUpdateDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile file) {

        return ResponseEntity.ok(managerService.updateModerator(id, dto, file));
    }

    @PutMapping("/moderator/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePasswordModerator(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody PasswordChangeRequestDTO request){

        managerService.updatePasswordModerator(principal.getId(), request);

        return ResponseEntity.ok("Password updated successfully.");
    }

    @DeleteMapping("/moderators/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> fireModerator(@PathVariable Long id) {

        managerService.fireModerator(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/managers")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<ManagerUpdateDTO>> getAllManagers(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(managerService.listAllManagers(pageable));
    }

    @GetMapping("/manager/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerUpdateDTO> getManagerById(@PathVariable Long id){

        return ResponseEntity.ok(managerService.getManagerById(id));
    }

    @PutMapping("/manager/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePasswordManager(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody @Valid PasswordChangeRequestDTO request){

        managerService.updatePasswordManager(principal.getId(), request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/managers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerUpdateDTO> hireManager(@RequestPart("data") @Valid ManagerCreateDTO dto, @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.status(201).body(managerService.hireManager(dto, file));
    }

    @PutMapping(value = "/managers/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerUpdateDTO> updateManager(@PathVariable Long id, @RequestPart("data") @Valid ManagerUpdateDTO dto,
        @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.ok(managerService.updateManager(id, dto, file));
    }
    
    @DeleteMapping("/managers/{id}")
    @PreAuthorize("hasRole('MANAGER') and #id != principal.id")
    public ResponseEntity<Void> fireManager(@PathVariable Long id){

        managerService.fireManager(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/search")
    @PreAuthorize("hasRole('MANAGER') or hasRole('MODERATOR')")
    public ResponseEntity<Page<UserAdminDTO>> searchUsers(@RequestParam String name, @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(managerService.searchUsersByName(name, pageable));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('MODERATOR')")
    public ResponseEntity<UserAdminDTO> updateUserInfo(@PathVariable Long id, @RequestBody UserAdminDTO user){

        return ResponseEntity.ok(managerService.updateUserInfo(id, user));
        
    }
}
