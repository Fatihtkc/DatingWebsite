package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Manager;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllActiveUsers() {
        return userRepository.findByApprovedTrueAndBannedFalse();
    }

    @Override
    public boolean checkUser(String email, String username){

        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
    
        // Username kontrolü
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            throw new UniqueConstraintViolationException("Username is already in use");
        }

        return true;
    }

    @Override
    public User createUser(User user) {

        String plainPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
    

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);

        // Temel kimlik bilgileri
        existingUser.setUsername(user.getUsername());
        existingUser.setFullName(user.getFullName());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setGender(user.getGender());

        // Giriş bilgileri
        existingUser.setEmail(user.getEmail());
        // Eğer şifre güncelleniyorsa, encode edin:
        // existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setPassword(user.getPassword());

        // Fiziksel özellikler
        existingUser.setHeight(user.getHeight());
        existingUser.setWeight(user.getWeight());
        existingUser.setBodyType(user.getBodyType());

        // Konum
        existingUser.setLocation(user.getLocation());

        // İlişki tercihleri
        existingUser.setRelationshipType(user.getRelationshipType());
        existingUser.setAgePreference(user.getAgePreference());
        existingUser.setDistancePreference(user.getDistancePreference());

        // Alışkanlıklar
        existingUser.setSmoke(user.getSmoke());
        existingUser.setAlcohol(user.getAlcohol());

        // Hakkında
        existingUser.setShorterbio(user.getShorterbio());

        // Diyet ve hobiler
        existingUser.setDiet(user.getDiet());
        existingUser.setHobbies(user.getHobbies());
        existingUser.setFavoriteMusic(user.getFavoriteMusic());
        existingUser.setWeekendPlans(user.getWeekendPlans());

        // Moderasyon ve rol
        existingUser.setApproved(user.isApproved());
        existingUser.setBanned(user.isBanned());
        existingUser.setPersonalityScore(user.getPersonalityScore());
        existingUser.setRole(user.getRole());

        // Resimler (OneToMany, orphanRemoval = true)
        existingUser.getImages().clear();
        if (user.getImages() != null) {
            for (UserImage img : user.getImages()) {
                img.setUser(existingUser);
                existingUser.getImages().add(img);
            }
        }

        existingUser.setLatitude(user.getLatitude());
        existingUser.setLongitude(user.getLongitude());

        // Son olarak kaydet
        return userRepository.save(existingUser);
    }

    @Override
    public List<User> getNearbyUsers(double lat, double lon, double distance) {
        return userRepository.findUsersWithinDistance(lat, lon, distance);
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

