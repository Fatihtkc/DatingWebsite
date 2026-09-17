package com.datingapp.backend.controller;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.LoginService;
import com.datingapp.backend.dto.JwtResponse;
import com.datingapp.backend.dto.LoginRequest;
import com.datingapp.backend.enums.Role;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS })
@RequiredArgsConstructor
public class AuthenticationController {

    private final LoginService loginService;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login request received for email: " + loginRequest.getEmail());

            Object foundEntity = null;
            Role role = null;
            Long id = null;
            String username = null;

            // 1. Önce User sınıfında arıyoruz.
            var optionalUser = loginService.findUserByEmail(loginRequest.getEmail());
            if (optionalUser.isPresent()) {
                foundEntity = optionalUser.get();
                role = Role.USER;
            } else {
                // 2. Moderator kontrolü
                var optionalModerator = loginService.findModeratorByEmail(loginRequest.getEmail());
                if (optionalModerator.isPresent()) {
                    foundEntity = optionalModerator.get();
                    role = Role.MODERATOR;
                } else {
                    // 3. Manager kontrolü
                    var optionalManager = loginService.findManagerByEmail(loginRequest.getEmail());
                    if (optionalManager.isPresent()) {
                        foundEntity = optionalManager.get();
                        role = Role.MANAGER;
                    }
                }
            }

            if (foundEntity == null) {
                System.out.println("User/Moderator/Manager not found for email: " + loginRequest.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }

            // Şifreyi alın
            String storedPassword = "";
            String email = loginRequest.getEmail(); // Kullanıcı adını genelde e-posta olarak alıyoruz
            if (foundEntity instanceof User) {
                storedPassword = ((User) foundEntity).getPassword();
                id = ((User) foundEntity).getId();
                username = ((User) foundEntity).getUsername();
            } else if (foundEntity instanceof Moderator) {
                storedPassword = ((Moderator) foundEntity).getPassword();
                id = ((Moderator) foundEntity).getId();
                username = ((Moderator) foundEntity).getEmail();
            } else if (foundEntity instanceof Manager) {
                storedPassword = ((Manager) foundEntity).getPassword();
                id = ((Manager) foundEntity).getId();
                username = ((Manager) foundEntity).getEmail();
            }

            // 4. Şifre doğrulaması yapalım
            if (!passwordEncoder.matches(loginRequest.getPassword(), storedPassword)) {
                throw new BadCredentialsException("Invalid email or password");
            }
            System.out.println("Password match confirmed.");

            // 5. CustomUserDetails oluşturup token üreteceğiz
            CustomUserDetails userDetails = new CustomUserDetails(id, username, storedPassword, role);
            String jwt = jwtUtil.generateJwtToken(userDetails);

            // Yanıt olarak token ve rol bilgisini gönderiyoruz.
            return ResponseEntity.ok(new JwtResponse(id,username,jwt, role));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401)
            .body("Invalid email or password");
        } catch (Exception ex) {
            return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Internal Server Error");
        }
    }
}


