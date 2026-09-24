package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.service.UserBlockService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserBlockService userBlockService;

    @Override
    public Page<UserProfileDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toDTO);
    }

    @Override
    public UserProfileDTO getUserById(Long id, Long requesterId) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userBlockService.isBlockedBetween(requesterId, id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        return userMapper.toDTO(user);
    }

    private User getUserEntityById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "User not found with id: " + id
                    )
            );
    }

    @Override
    public Page<UserProfileDTO> getAllActiveUsers(Pageable pageable) {
        return userRepository.findByApprovedTrueAndBannedFalse(pageable)
            .map(userMapper::toDTO);
    }

    @Override
    public boolean checkUser(String email, String username){

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UniqueConstraintViolationException("Email is already in use");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UniqueConstraintViolationException("Username is already in use");
        }
        return true;

    }

    @Override
    public User createUser(UserCreateDTO dto) {

        User user = userMapper.toEntity(dto);

        checkUser(user.getEmail(), user.getUsername());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
    

    @Override
    public UserProfileDTO updateUser(Long id, UserUpdateDTO dto){

        User existingUser = getUserEntityById(id);

        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setBirthDate(dto.getBirthDate());
        existingUser.setGender(dto.getGender());

        existingUser.setHeight(dto.getHeight());
        existingUser.setWeight(dto.getWeight());
        existingUser.setBodyType(dto.getBodyType());

        existingUser.setLocation(dto.getLocation());

        existingUser.setRelationshipType(dto.getRelationshipType());
        existingUser.setAgePreference(dto.getAgePreference());
        existingUser.setDistancePreference(dto.getDistancePreference());

        existingUser.setSmoke(dto.getSmoke());
        existingUser.setAlcohol(dto.getAlcohol());

        existingUser.setShorterBio(dto.getShorterBio());

        existingUser.setDiet(dto.getDiet());
        existingUser.setHobbies(dto.getHobbies());
        existingUser.setFavoriteMusic(dto.getFavoriteMusic());
        existingUser.setWeekendPlans(dto.getWeekendPlans());

        User updatedUser = userRepository.save(existingUser);

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public Page<NearbyUserDTO> getNearbyUsers(Long requesterId, double latitude, double longitude, double distance, Pageable pageable){

        return userRepository
                .findUsersWithinDistance(requesterId, latitude, longitude, distance, pageable)
                .map(userMapper::toNearbyDTO);

    }

    @Override
    @Transactional
    public UserAdminDTO adminUpdateUser(Long id, UserAdminDTO dto){
        User existingUser = getUserEntityById(id);

        existingUser.setApproved(dto.isApproved());
        existingUser.setBanned(dto.isBanned());
        existingUser.setConfirmed(dto.isConfirmed());
        existingUser.setPersonalityScore(dto.getPersonalityScore());
        existingUser.setRole(dto.getRole());

        User saved = userRepository.save(existingUser);
        return userMapper.toAdminDTO(saved);
    }

    @Override
    @Transactional
    public void changePassword(Long id, String newPlainPassword){
        User existingUser = getUserEntityById(id);
        existingUser.setPassword(passwordEncoder.encode(newPlainPassword));
        userRepository.save(existingUser);
    }


    @Override
    @Transactional
    public UserProfileDTO updateUserImages(Long id, List<UserImage> images){
        User existingUser = getUserEntityById(id);

        existingUser.getImages().clear();
        if (images != null) {
            for (UserImage img : images) {
                img.setUser(existingUser);
                existingUser.getImages().add(img);
            }
        }

        User saved = userRepository.save(existingUser);
        return userMapper.toDTO(saved);
    }


    @Override
    @Transactional
    public void deleteUser(Long id){
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

}