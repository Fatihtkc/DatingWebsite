package com.datingapp.backend.controller;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.LoginService;
import com.datingapp.backend.dto.JwtResponse;
import com.datingapp.backend.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS })
public class AuthenticationController {

    @Autowired
    private LoginService loginService; // Servis üzerinden kullanıcı/moderator/manager bilgilerini getiriyoruz

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login request received for email: " + loginRequest.getEmail());

            Object foundEntity = null;
            String role = null;
            Long id = null;
            String username = null;

            // 1. Önce User sınıfında arıyoruz.
            var optionalUser = loginService.findUserByEmail(loginRequest.getEmail());
            if (optionalUser.isPresent()) {
                foundEntity = optionalUser.get();
                role = "user";
            } else {
                // 2. Moderator kontrolü
                var optionalModerator = loginService.findModeratorByEmail(loginRequest.getEmail());
                if (optionalModerator.isPresent()) {
                    foundEntity = optionalModerator.get();
                    role = "moderator";
                } else {
                    // 3. Manager kontrolü
                    var optionalManager = loginService.findManagerByEmail(loginRequest.getEmail());
                    if (optionalManager.isPresent()) {
                        foundEntity = optionalManager.get();
                        role = "manager";
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
            } else if (foundEntity instanceof Manager) {
                storedPassword = ((Manager) foundEntity).getPassword();
                id = ((Manager) foundEntity).getId();
            }

            // 4. Şifre doğrulaması yapalım
            if (!passwordEncoder.matches(loginRequest.getPassword(), storedPassword)) {
                System.out.println("Password mismatch for email: " + loginRequest.getEmail());
                throw new BadCredentialsException("Invalid email or password");
            }
            System.out.println("Password match confirmed.");

            // 5. CustomUserDetails oluşturup token üreteceğiz
            CustomUserDetails userDetails = new CustomUserDetails(email, storedPassword, role);
            String jwt = jwtUtil.generateJwtToken(userDetails);
            System.out.println("JWT token generated: " + jwt);

            // Yanıt olarak token ve rol bilgisini gönderiyoruz.
            return ResponseEntity.ok(new JwtResponse(id,username,jwt, role));
        } catch (BadCredentialsException ex) {
            System.out.println("Authentication error: " + ex.getMessage());
            return ResponseEntity.status(401).body("Invalid email or password");
        } catch (Exception ex) {
            System.out.println("Unexpected error during authentication: " + ex.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error: " + ex.getMessage());
        }
    }
}


