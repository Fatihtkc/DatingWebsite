package com.datingapp.backend.controller;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;
import com.datingapp.backend.service.ManagerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;    

    @GetMapping("/moderators")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<ModeratorDTO>> getAllModerators(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(managerService.listAllModerators(pageable));
    }

    @GetMapping("/moderator/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('MODERATOR')")
    public ResponseEntity<ModeratorDTO> getModeratorById(@PathVariable Long id) {

        return ResponseEntity.ok(managerService.getModeratorById(id));
    }

    @PostMapping(value = "/moderators", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ModeratorDTO> hireModerator(@RequestPart("data") @Valid Moderator moderator, @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.status(201).body(managerService.hireModerator(moderator, file));
    }

    @PutMapping(value = "/moderators/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ModeratorDTO> updateModerator( @PathVariable Long id, @RequestPart("data") @Valid Moderator moderator,
        @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.ok(managerService.updateModerator(id, moderator, file));
    }

    @PutMapping("/moderator/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request){

        managerService.updatePassword(request);

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
    public ResponseEntity<Page<ManagerDTO>> getAllManagers(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(managerService.listAllManagers(pageable));
    }

    @GetMapping("/manager/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDTO> getManagerById(@PathVariable Long id){

        return ResponseEntity.ok(managerService.getManagerById(id));
    }

    @PutMapping("/manager/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePasswordManager(@RequestBody PasswordChangeRequest request){

        boolean success = managerService.updatePasswordManager(request);

        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully."));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Old password is incorrect."));
        }
    }

    @PostMapping(value = "/managers", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDTO> hireManager(@RequestPart("data") @Valid Manager manager, @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.status(201).body(managerService.hireManager(manager, file));
    }

    @PutMapping(value = "/managers/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDTO> updateManager(@PathVariable Long id, @RequestPart("data") @Valid Manager manager,
        @RequestPart(value = "image", required = false) MultipartFile file){

        return ResponseEntity.ok(managerService.updateManager(id, manager, file));
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
    public ResponseEntity<UserAdminDTO> updateUserInfo(@PathVariable Long id, @RequestBody User user){

        return ResponseEntity.ok(managerService.updateUserInfo(id, user));
    }
}
