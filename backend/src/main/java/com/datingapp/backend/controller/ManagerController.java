package com.datingapp.backend.controller;

import com.datingapp.backend.dto.ManagerDTO;
import com.datingapp.backend.dto.ModeratorDTO;
import com.datingapp.backend.dto.PasswordChangeRequest;
import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.User;
import com.datingapp.backend.service.ManagerService;
import com.datingapp.backend.service.FileStorageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService adminService;
    private final FileStorageService storage;
    
    // --- Moderatör İşlemleri ---
    // Tüm moderatörleri getir
    @GetMapping("/moderators")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ModeratorDTO>> getAllModerators() {
        return ResponseEntity.ok(adminService.listAllModerators());
    }

    // Belirli bir moderatörü ID ile getir
    @GetMapping("/moderator/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('MODERATOR')")
    public ResponseEntity<ModeratorDTO> getModeratorById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getModeratorById(id));
    }

    // Yeni moderatör oluştur
    @PostMapping(value = "/moderators", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ModeratorDTO> hireModerator(
        @RequestPart("data") @Valid Moderator moderator,
        @RequestPart(value = "image", required = false) MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            String filename = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()      // http://localhost:8080
                .path(filename)                // /uploads/uuid_name.png
                .toUriString();                // tam URL string
            moderator.setImageUrl(publicUrl);
        }

        return ResponseEntity.status(201).body(adminService.hireModerator(moderator));
    }

    // Moderatör güncelle
    @PutMapping(value = "/moderators/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ModeratorDTO> updateModerator(
        @PathVariable Long id,
        @RequestPart("data") @Valid Moderator moderator,
        @RequestPart(value = "image", required = false) MultipartFile file) {

            if (file != null && !file.isEmpty()) {
                // 1) Dosyayı kaydet
                String filename = storage.storeFile(file);
    
                // 2) Burada public URL’i oluştur
                String publicUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()      // http://localhost:8080
                    .path(filename)                // /uploads/uuid_name.png
                    .toUriString();                // tam URL string
    
                // 3) Oluşan URL’i entity’ye set et
                moderator.setImageUrl(publicUrl);
            }

        return ResponseEntity.ok(adminService.updateModerator(id, moderator));
    }

    // Moderatör kedni şifresini güncelle (sadece şifre güncelleme örneği)
    @PutMapping("/moderator/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        boolean success = adminService.updatePassword(request);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully."));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Old password is incorrect."));
        }
    }

    @DeleteMapping("/moderators/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> fireModerator(@PathVariable Long id) {
        adminService.fireModerator(id);
        return ResponseEntity.noContent().build();
    }

    // --- Manager İşlemleri ---
    // Tüm managerları getir
    @GetMapping("/managers")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<ManagerDTO>> getAllManagers() {
        return ResponseEntity.ok(adminService.listAllManagers());
    }

    // Belirli bir managerı ID ile getir
    @GetMapping("/manager/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDTO> getManagerById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getManagerById(id));
    }

    @PutMapping("/manager/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePasswordManager(@RequestBody PasswordChangeRequest request) {
        boolean success = adminService.updatePasswordManager(request);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Password updated successfully."));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "Old password is incorrect."));
        }
    }

    // Yeni manager oluştur
    @PostMapping(value = "/managers", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDTO> hireManager(
        @RequestPart("data") @Valid Manager manager,
        @RequestPart(value = "image", required = false) MultipartFile file) {

        if (file != null && !file.isEmpty()) {
            // 1) Dosyayı kaydet
            String filename = storage.storeFile(file);

            // 2) Burada public URL’i oluştur
            String publicUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()      // http://localhost:8080
                .path(filename)                // /uploads/uuid_name.png
                .toUriString();                // tam URL string

            // 3) Oluşan URL’i entity’ye set et
            manager.setImageUrl(publicUrl);
        }

        return ResponseEntity.status(201).body(adminService.hireManager(manager));
    }

    // Manager güncelle
    @PutMapping(value = "/managers/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ManagerDTO> updateManager(
        @PathVariable Long id,
        @RequestPart("data") @Valid Manager manager,
        @RequestPart(value = "image", required = false) MultipartFile file) {
    
        if (file != null && !file.isEmpty()) {
            String filename = storage.storeFile(file);
            String publicUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()      // http://localhost:8080
                .path(filename)                // /uploads/uuid_name.png
                .toUriString();                // tam URL string
            manager.setImageUrl(publicUrl);
        }

        return ResponseEntity.ok(adminService.updateManager(id, manager));
    }
    

    // Manager sil
    @DeleteMapping("/managers/{id}")
    @PreAuthorize("#id != principal.id or hasRole('MANAGER')")
    public ResponseEntity<Void> fireManager(@PathVariable Long id) {
        adminService.fireManager(id);
        return ResponseEntity.noContent().build();
    }

    // --- Kullanıcı Arama ve Güncelleme ---
    @GetMapping("/users/search")
    public ResponseEntity<List<UserAdminDTO>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(adminService.searchUsersByName(name));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserAdminDTO> updateUserInfo(@PathVariable Long id,
                                                       @RequestBody User user) {
        return ResponseEntity.ok(adminService.updateUserInfo(id, user));
    }
}
