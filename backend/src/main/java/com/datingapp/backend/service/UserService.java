package com.datingapp.backend.service;

import com.datingapp.backend.model.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> getAllActiveUsers();
    boolean checkUser(String email, String username);
    List<User> getNearbyUsers(double lat, double lon, double distance);

}
