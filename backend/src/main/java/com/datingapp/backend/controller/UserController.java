package com.datingapp.backend.controller;

import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.enums.Role;
import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.model.User;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.LoginService;
import com.datingapp.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getMyProfile(@AuthenticationPrincipal CustomUserDetails principal){

        Long userId = principal.getId();

        return ResponseEntity.ok(userService.getUserById(userId, principal.getId()));

    }

    @GetMapping("/profiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getAllUsers(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userService.getAllUsers(pageable));

    }

    @GetMapping("/users/active") 
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDTO>> getAll(@PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userService.getAllActiveUsers(pageable));

    }

    @GetMapping("/users/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getById(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id, principal.getId()));
    }

    @GetMapping("/profile/{email}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getByEmail(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable String email){

        Optional<User> optionalUser = loginService.findUserByEmail(email);

        return optionalUser
            .map(user -> ResponseEntity.ok(userService.getUserById(user.getId(),principal.getId())))
            .orElseGet(() ->ResponseEntity.notFound().build());

    }

    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam String email, @RequestParam String username){

        try {
            userService.checkUser(email, username);
            return ResponseEntity.ok().build();
        } catch (UniqueConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> createUser(@RequestBody @Valid UserCreateDTO dto){
        User createdUser = userService.createUser(dto);

        CustomUserDetails userDetails = new CustomUserDetails(createdUser.getId(), createdUser.getUsername(), createdUser.getPassword(), createdUser.getRole());
        String jwt = jwtUtil.generateJwtToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt, createdUser.getRole()));
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("#id == principal.id")
    public ResponseEntity<UserProfileDTO> update(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("#id == principal.id or hasRole('MODERATOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id){

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();

    }
    
    @GetMapping("/nearby")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<NearbyUserDTO>> getNearbyUsers(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam double lat,
            @RequestParam double lon, @RequestParam(defaultValue = "50") double distanceKm, @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(userService.getNearbyUsers(principal.getId(), lat, lon, distanceKm, pageable));

    }

    class JwtResponse {

        private String token;
        private Role role;

        public JwtResponse(String token, Role role) {
            this.token = token;
            this.role = role;
        }
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public Role getRole() {
            return role;
        }
        public void setRole(Role role) {
            this.role = role;
        }
    }
}
