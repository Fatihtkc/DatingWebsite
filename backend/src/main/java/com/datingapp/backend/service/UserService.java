package com.datingapp.backend.service;

import com.datingapp.backend.dto.JwtResponseDTO;
import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.model.UserImage;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserProfileDTO> getAllUsers(Pageable pageable);
    UserProfileDTO getUserById(Long id, Long requesterId);
    JwtResponseDTO createUser(UserCreateDTO dto);
    UserProfileDTO updateUser(Long id, UserUpdateDTO dto);
    void deleteUser(Long id);
    Page<UserProfileDTO> getAllActiveUsers(Pageable pageable);
    boolean checkUser(String email, String username);
    Page<NearbyUserDTO> getNearbyUsers(Long requesterId, double lat, double lon, double distance, Pageable pageable);
    void changePassword(Long id, String newPlainPassword);
    UserProfileDTO updateUserImages(Long id, List<UserImage> images);
}
