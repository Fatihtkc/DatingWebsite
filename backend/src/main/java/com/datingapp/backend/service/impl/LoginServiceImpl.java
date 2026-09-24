package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.dto.JwtResponseDTO;
import com.datingapp.backend.dto.LoginRequest;
import com.datingapp.backend.enums.Role;
import com.datingapp.backend.exception.TooManyLoginAttemptsException;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.ManagerRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.security.PasswordUtil;
import com.datingapp.backend.service.LoginAttemptService;
import com.datingapp.backend.service.LoginService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final ModeratorRepository moderatorRepository;
    private final ManagerRepository managerRepository;
    private final PasswordUtil passwordUtil;
    private final LoginAttemptService loginAttemptService;
    private final JwtUtil jwtUtil;

    private record LoginUser(Long id, String username, String password, Role role) {}

    @Override
    public JwtResponseDTO login(LoginRequest loginRequest, String ip){

        String email = loginRequest.getEmail();

        if (loginAttemptService.isLocked(email, ip)) {
            throw new TooManyLoginAttemptsException("Too many failed attempts. Please try again later.");
        }

        LoginUser loginUser = findLoginUser(email);

        if (!passwordUtil.checkPassword(
                loginRequest.getPassword(),
                loginUser.password())) {

            loginAttemptService.recordAttempt(email, ip, false);

            throw new BadCredentialsException("Invalid email or password");
        }

        loginAttemptService.recordAttempt(email, ip, true);

        CustomUserDetails userDetails = new CustomUserDetails(loginUser.id(), email, loginUser.password(), loginUser.role());

        String jwt = jwtUtil.generateJwtToken(userDetails);

        return new JwtResponseDTO(loginUser.id(), loginUser.username(), jwt, loginUser.role());
    }

    private LoginUser findLoginUser(String email){

        return userRepository.findByEmail(email)
                .map(user -> new LoginUser(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        Role.USER
                ))
                .orElseGet(() ->
                        moderatorRepository.findByEmail(email)
                                .map(moderator -> new LoginUser(
                                        moderator.getId(),
                                        moderator.getEmail(),
                                        moderator.getPassword(),
                                        Role.MODERATOR
                                ))
                                .orElseGet(() ->
                                        managerRepository.findByEmail(email)
                                                .map(manager -> new LoginUser(
                                                        manager.getId(),
                                                        manager.getEmail(),
                                                        manager.getPassword(),
                                                        Role.MANAGER
                                                ))
                                                .orElseThrow(() ->new BadCredentialsException("Invalid email or password")
                                )
                ));
    }

    @Override
    public User registerUser(String email, String rawPassword) {
        String encodedPassword = passwordUtil.encodePassword(rawPassword);
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<Moderator> findModeratorByEmail(String email) {
        return moderatorRepository.findByEmail(email);
    }
    
    @Override
    public Optional<Manager> findManagerByEmail(String email) {
        return managerRepository.findByEmail(email);
    }

    @Override
    public boolean validatePassword(String rawPassword, String storedPassword) {
        return passwordUtil.checkPassword(rawPassword, storedPassword);
    }

}
