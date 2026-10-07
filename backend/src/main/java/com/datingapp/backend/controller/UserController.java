package com.datingapp.backend.controller;

import com.datingapp.backend.dto.JwtResponseDTO;
import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getMyProfile(@AuthenticationPrincipal CustomUserDetails principal){

        return ResponseEntity.ok(userService.getUserById(principal.getId()));
    }

    @GetMapping("/allprofiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getAllUsers(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/profiles") 
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getAllActiveUsers(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userService.getAllActiveUsers(pageable));

    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getById(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id, principal.getId()));
    }

    @GetMapping("/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getByEmail(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable String email){

        return ResponseEntity.ok(userService.getUserByEmail(email, principal.getId()));
    }

    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam String email, @RequestParam String username){

        userService.checkUser(email, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<JwtResponseDTO> createUser(@RequestBody @Valid UserCreateDTO dto){
        
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("#id == principal.id")
    public ResponseEntity<UserProfileDTO> update(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == principal.id or hasRole('MODERATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();

    }
    
    @GetMapping("/nearby")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NearbyUserDTO>> getNearbyUsers(
        @AuthenticationPrincipal CustomUserDetails principal, 
        @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,
        @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lon,
        @RequestParam(defaultValue = "50") @DecimalMin("0.1") @DecimalMax("100") double distanceKm,
        @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userService.getNearbyUsers(principal.getId(), lat, lon, distanceKm, pageable));

    }

}
