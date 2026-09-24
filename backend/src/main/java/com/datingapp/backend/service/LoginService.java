package com.datingapp.backend.service;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.dto.JwtResponseDTO;
import com.datingapp.backend.dto.LoginRequest;
import com.datingapp.backend.model.Manager;

import java.util.Optional;

public interface LoginService {

    User registerUser(String email, String rawPassword);

    Optional<User> findUserByEmail(String email);
    
    Optional<Moderator> findModeratorByEmail(String email);
    
    Optional<Manager> findManagerByEmail(String email);

    boolean validatePassword(String rawPassword, String storedPassword);

    JwtResponseDTO login(LoginRequest loginRequest, String ip);
}
