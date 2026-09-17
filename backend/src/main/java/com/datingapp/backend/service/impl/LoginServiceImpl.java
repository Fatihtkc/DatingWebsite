package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.Moderator;
import com.datingapp.backend.model.Manager;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.repository.ModeratorRepository;
import com.datingapp.backend.repository.ManagerRepository;
import com.datingapp.backend.security.PasswordUtil;
import com.datingapp.backend.service.LoginService;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final ModeratorRepository moderatorRepository;
    private final ManagerRepository managerRepository;

    @Override
    public User registerUser(String email, String rawPassword) {
        // Şifreyi şifrele
        String encodedPassword = PasswordUtil.encodePassword(rawPassword);
        // Kullanıcıyı oluştur ve kaydet
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        // Veritabanından User'ı bul
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<Moderator> findModeratorByEmail(String email) {
        // Veritabanından Moderator'ü bul
        return moderatorRepository.findByEmail(email);
    }
    
    @Override
    public Optional<Manager> findManagerByEmail(String email) {
        // Veritabanından Manager'ı bul
        return managerRepository.findByEmail(email);
    }

    @Override
    public boolean validatePassword(String rawPassword, String storedPassword) {
        // Şifreyi doğrula
        return PasswordUtil.checkPassword(rawPassword, storedPassword);
    }
}
