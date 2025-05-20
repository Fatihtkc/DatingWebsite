package com.datingapp.backend.controller;

import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.model.User;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.LoginService;
import com.datingapp.backend.service.UserService;
import jakarta.validation.Valid;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // React uygulamanın adresi
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtil jwtUtil;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Tüm kullanıcıları getir
    @GetMapping("/profiles") // Change the path
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Aktif kullanıcıları getir
    @GetMapping("/users/active") // Keep the original path
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAllActiveUsers());
    }

    // Belirli bir kullanıcıyı ID ile getir
    @GetMapping("/users/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        Optional<User> optionalUser = loginService.findUserByEmail(email);

        return optionalUser
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestParam String email, @RequestParam String username) {
        try {
            userService.checkUser(email, username);
            return ResponseEntity.ok().build();
        } catch (UniqueConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Yeni kullanıcı oluştur
    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> createUser(@RequestBody @Valid User user) {
        User createdUser = userService.createUser(user);

        // 3. Kayıt olan kullanıcıyı UserDetails olarak yükle
        CustomUserDetails userDetails = new CustomUserDetails(createdUser.getUsername(), createdUser.getPassword(), createdUser.getRole());
        String jwt = jwtUtil.generateJwtToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt, createdUser.getRole()));
    }

    // Kullanıcı güncelle
    @PutMapping("/profile/{id}")
    @PreAuthorize("#id == principal.id") // kendi profili
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // Kullanıcı sil
    @DeleteMapping("/users/{id}")
    @PreAuthorize("#id == principal.id or hasRole('moderator')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<User>> getNearbyUsers(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1000") double distanceKm) {
        return ResponseEntity.ok(userService.getNearbyUsers(lat, lon, distanceKm));
    }

    // JWT yanıtı için DTO: Token'ın yanında rol bilgisini de gönderiyoruz.
    class JwtResponse {
        private String token;
        private String role;

        public JwtResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
    }
}
