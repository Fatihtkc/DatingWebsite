package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.EmailVerification;
import com.datingapp.backend.model.User;
import com.datingapp.backend.model.UserImage;
import com.datingapp.backend.repository.EmailVerificationRepository;
import com.datingapp.backend.repository.UserImageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.security.JwtUtil;
import com.datingapp.backend.service.UserService;

import lombok.RequiredArgsConstructor;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datingapp.backend.dto.JwtResponseDTO;
import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.exception.InvalidOtpException;
import com.datingapp.backend.exception.ResourceNotFoundException;
import com.datingapp.backend.exception.UniqueConstraintViolationException;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.UserMapper;
import com.datingapp.backend.service.FileStorageService;
import com.datingapp.backend.service.RefreshTokenService;
import com.datingapp.backend.service.UserBlockService;
import com.datingapp.backend.service.UserImageService;
import com.datingapp.backend.service.LoginService;
import com.datingapp.backend.records.BoundingBox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserBlockService userBlockService;
    private final RefreshTokenService refreshTokenService;
    private final LoginService loginService;
    private final FileStorageService storage;
    private final UserImageRepository userImageRepository;
    private final JwtUtil jwtUtil;
    private final UserImageService userImageService;
    private final EmailVerificationRepository emailVerificationRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> getAllUsers(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);

        Map<Long, List<UserImage>> imagesByUserId = userImageService.loadApprovedImages(users.getContent());

        return users.map(user -> userMapper.toDTO(user, imagesByUserId.getOrDefault(user.getId(), List.of())));
    }
    
    @Override
    public UserProfileDTO getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    public UserProfileDTO getUserById(Long id, Long requesterId){

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userBlockService.isBlockedBetween(requesterId, id)){
            throw new UserNotFoundException("User not found with id: " + id);
        }

        return userMapper.toDTO(user);
    }

    @Override
    public UserProfileDTO getUserByEmail(String email, Long requesterId){
        User user = loginService.findUserByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return getUserById(user.getId(), requesterId);
    }

    private User getUserEntityById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public Page<UserProfileDTO> getAllActiveUsers(Pageable pageable){
        Page<User> users = userRepository.findByApprovedTrueAndBannedFalse(pageable);

        Map<Long, List<UserImage>> imagesByUserId = userImageService.loadApprovedImages(users.getContent());

        return users.map(user -> userMapper.toDTO(user, imagesByUserId.getOrDefault(user.getId(), List.of())));
    }

    @Override
    public boolean checkUser(String email, String username){

        if (userRepository.findByEmail(email).isPresent()){
            throw new UniqueConstraintViolationException("Email is already in use");
        }
        if (userRepository.findByUsername(username).isPresent()){
            throw new UniqueConstraintViolationException("Username is already in use");
        }
        return true;

    }

    @Override
    @Transactional 
    public JwtResponseDTO createUser(UserCreateDTO dto){

        String email = dto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        dto.setEmail(email);

        EmailVerification verification = emailVerificationRepository
            .findByEmailForUpdate(email).orElseThrow(() -> new InvalidOtpException("Email verification is required."));

        if (!verification.getExpiresAt().isAfter(LocalDateTime.now())) {
            emailVerificationRepository.delete(verification);

            throw new InvalidOtpException("Email verification has expired.");
        }

        User createdUser = userMapper.toEntity(dto);

        checkUser(createdUser.getEmail(), createdUser.getUsername());

        createdUser.setPassword(passwordEncoder.encode(createdUser.getPassword()));

        createdUser.setLocationPoint(createLocationPoint(dto.getLongitude(), dto.getLatitude()));

        userRepository.save(createdUser);

        CustomUserDetails userDetails = new CustomUserDetails(
            createdUser.getId(), createdUser.getEmail(), createdUser.getPassword(), createdUser.getRole(), false);

        String jwt = jwtUtil.generateJwtToken(userDetails);
        String refreshToken = refreshTokenService.create(userDetails.getUsername());

        emailVerificationRepository.delete(verification);

        return new JwtResponseDTO(createdUser.getId(), createdUser.getUsername(), jwt, refreshToken, createdUser.getRole());
    }
    

    @Override
    public UserProfileDTO updateUser(Long id, UserUpdateDTO dto){

        User existingUser = getUserEntityById(id);

        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setBirthDate(dto.getBirthDate());
        existingUser.setGender(dto.getGender());

        existingUser.setHeight(dto.getHeight());
        existingUser.setBodyType(dto.getBodyType());

        existingUser.setLocation(dto.getLocation());

        if (dto.getLatitude() != null || dto.getLongitude() != null){

            if (dto.getLatitude() == null || dto.getLongitude() == null) {
                throw new IllegalArgumentException("Latitude and longitude must be provided together.");
            }

            existingUser.setLocationPoint(createLocationPoint(dto.getLongitude(), dto.getLatitude()));
        }

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
    @Transactional(readOnly = true)
    public Page<NearbyUserDTO> getNearbyUsers(Long requesterId, double latitude, double longitude, double distance, Pageable pageable){

        BoundingBox bounds = calculateBoundingBox(latitude, longitude, distance);

        return userRepository.findUsersWithinDistance(
            requesterId, latitude, longitude, distance, bounds.minLatitude(), bounds.maxLatitude(), bounds.minLongitude()
            ,bounds.maxLongitude(), bounds.crossesAntimeridian(), bounds.allLongitudes(), pageable
        ).map(userMapper::toNearbyDTO);
    }


    @Override
    @Transactional
    public void changePassword(Long id, String newPlainPassword){
        User existingUser = getUserEntityById(id);
        existingUser.setPassword(passwordEncoder.encode(newPlainPassword));
        userRepository.save(existingUser);
        refreshTokenService.revokeAll(existingUser.getEmail());
    }


    @Override
    @Transactional
    public UserProfileDTO updateUserImages(Long id, List<UserImage> images){
        User existingUser = getUserEntityById(id);

        existingUser.getImages().clear();
        if (images != null){
            for (UserImage img : images){
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

        if (!userRepository.existsById(id)){
            throw new UserNotFoundException("User not found with id: " + id);
        }
        
        List<UserImage> existingImages = userImageRepository.findByUserId(id);
        userRepository.deleteById(id);
        for (UserImage image : existingImages){
            storage.deleteFile(image.getImageUrl());
        }
    }

    private Point createLocationPoint(double longitude, double latitude){
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    private BoundingBox calculateBoundingBox(double latitude, double longitude, double distanceKm){

        if (!Double.isFinite(latitude) || !Double.isFinite(longitude) || !Double.isFinite(distanceKm)
                || latitude < -90 || latitude > 90|| longitude < -180 || longitude > 180 || distanceKm < 0){
            throw new IllegalArgumentException("Invalid coordinates or distance.");
        }

        double latDelta = distanceKm / 110.574;

        double minLat = Math.max(-90.0, latitude - latDelta);
        double maxLat = Math.min(90.0, latitude + latDelta);

        boolean reachesPole = minLat <= -90.0 || maxLat >= 90.0;

        if(reachesPole){
            return new BoundingBox(minLat, maxLat, -180.0, 180.0, false, true);
        }

        double maxLatitudeRadians = Math.toRadians(Math.max(Math.abs(minLat), Math.abs(maxLat)));

        double cos = Math.cos(maxLatitudeRadians);

        if(cos <= 0){
            return new BoundingBox(minLat, maxLat, -180.0, 180.0, false, true);
        }

        double lonDelta = distanceKm / (111.320 * cos);

        if(lonDelta >= 180.0){
            return new BoundingBox(minLat, maxLat, -180.0, 180.0, false, true);
        }

        double rawMinLon = longitude - lonDelta;
        double rawMaxLon = longitude + lonDelta;

        boolean crosses = rawMinLon < -180.0 || rawMaxLon > 180.0;

        double minLon = rawMinLon < -180.0 ? rawMinLon + 360.0 : rawMinLon;

        double maxLon = rawMaxLon > 180.0 ? rawMaxLon - 360.0 : rawMaxLon;

        return new BoundingBox(minLat, maxLat, minLon, maxLon, crosses, false);
    }

}