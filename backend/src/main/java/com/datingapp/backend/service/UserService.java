package com.datingapp.backend.service;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;

import java.util.List;

public interface UserService {
    List<UserProfileDTO> getAllUsers();
    UserProfileDTO getUserById(Long id);
    User createUser(UserCreateDTO dto);
    UserProfileDTO updateUser(Long id, UserUpdateDTO dto);
    void deleteUser(Long id);
    List<UserProfileDTO> getAllActiveUsers();
    boolean checkUser(String email, String username);
    List<NearbyUserDTO> getNearbyUsers(double lat, double lon, double distance);
    UserAdminDTO adminUpdateUser(Long id, UserAdminDTO dto);
    void changePassword(Long id, String newPlainPassword);
    public UserProfileDTO updateUserImages(Long id, List<UserImage> images);
}
