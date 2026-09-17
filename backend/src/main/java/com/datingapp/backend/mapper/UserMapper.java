package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.UserAdminDTO;
import com.datingapp.backend.dto.User.NearbyUserDTO;
import com.datingapp.backend.dto.User.UserCreateDTO;
import com.datingapp.backend.dto.User.UserProfileDTO;
import com.datingapp.backend.dto.User.UserUpdateDTO;
import com.datingapp.backend.enums.Alcohol;
import com.datingapp.backend.enums.BodyType;
import com.datingapp.backend.enums.Diet;
import com.datingapp.backend.enums.Gender;
import com.datingapp.backend.enums.RelationshipType;
import com.datingapp.backend.enums.Smoking;
import com.datingapp.backend.model.User;
import com.datingapp.backend.projection.NearbyUserProjection;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ImageMapper userImageMapper;

    public UserProfileDTO toDTO(User user) {

        UserProfileDTO dto = new UserProfileDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setBodyType(user.getBodyType());
        dto.setLocation(user.getLocation());
        dto.setRelationshipType(user.getRelationshipType());
        dto.setAlcohol(user.getAlcohol());
        dto.setSmoke(user.getSmoke());
        dto.setDiet(user.getDiet());
        dto.setShorterBio(user.getShorterBio());
        dto.setHobbies(user.getHobbies());
        dto.setFavoriteMusic(user.getFavoriteMusic());
        dto.setWeekendPlans(user.getWeekendPlans());
        if (user.getImages() != null) {
            dto.setImages(
                user.getImages()
                    .stream()
                    .map(userImageMapper::toDTO)
                    .toList()
            );
        }
        return dto;
    }

    public UserCreateDTO toCreateDTO(User user) {

        UserCreateDTO dto = new UserCreateDTO();

        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setBodyType(user.getBodyType());
        dto.setLocation(user.getLocation());
        dto.setRelationshipType(user.getRelationshipType());
        dto.setAlcohol(user.getAlcohol());
        dto.setSmoke(user.getSmoke());
        dto.setDiet(user.getDiet());
        dto.setShorterBio(user.getShorterBio());
        dto.setHobbies(user.getHobbies());
        dto.setFavoriteMusic(user.getFavoriteMusic());
        dto.setWeekendPlans(user.getWeekendPlans());
        dto.setAgePreference(user.getAgePreference());
        dto.setDistancePreference(user.getDistancePreference());

        return dto;
    }

    public UserUpdateDTO toUpdateDTO(User user) {

        UserUpdateDTO dto = new UserUpdateDTO();

        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setBodyType(user.getBodyType());
        dto.setLocation(user.getLocation());
        dto.setRelationshipType(user.getRelationshipType());
        dto.setAlcohol(user.getAlcohol());
        dto.setSmoke(user.getSmoke());
        dto.setDiet(user.getDiet());
        dto.setShorterBio(user.getShorterBio());
        dto.setHobbies(user.getHobbies());
        dto.setFavoriteMusic(user.getFavoriteMusic());
        dto.setWeekendPlans(user.getWeekendPlans());
        dto.setAgePreference(user.getAgePreference());
        dto.setDistancePreference(user.getDistancePreference());

        return dto;
    }

    public NearbyUserDTO toNearbyDTO(NearbyUserProjection projection) {

        NearbyUserDTO dto = new NearbyUserDTO();

        dto.setId(projection.getId());
        dto.setUsername(projection.getUsername());
        dto.setFirstName(projection.getFirstName());
        dto.setLastName(projection.getLastName());
        dto.setBirthDate(projection.getBirthDate());

        dto.setGender(
            projection.getGender() != null
                ? Gender.valueOf(projection.getGender())
                : null
        );

        dto.setBodyType(
            projection.getBodyType() != null
                ? BodyType.valueOf(projection.getBodyType())
                : null
        );

        dto.setRelationshipType(
            projection.getRelationshipType() != null
                ? RelationshipType.valueOf(
                    projection.getRelationshipType()
                )
                : null
        );

        dto.setSmoke(
            projection.getSmoke() != null
                ? Smoking.valueOf(projection.getSmoke())
                : null
        );

        dto.setAlcohol(
            projection.getAlcohol() != null
                ? Alcohol.valueOf(projection.getAlcohol())
                : null
        );

        dto.setDiet(
            projection.getDiet() != null
                ? Diet.valueOf(projection.getDiet())
                : null
        );

        dto.setShorterBio(projection.getShorterBio());

        dto.setDistance(projection.getDistance());

        return dto;
    }

    public UserAdminDTO toAdminDTO(User user) {
        UserAdminDTO dto = new UserAdminDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setApproved(user.isApproved());
        dto.setBanned(user.isBanned());
        dto.setConfirmed(user.isConfirmed());
        dto.setPersonalityScore(user.getPersonalityScore());
        dto.setRole(user.getRole());
        dto.setImages(
            user.getImages() != null
                ? user.getImages()
                    .stream()
                    .map(userImageMapper::toDTO)
                    .toList()
                : null
        );

        return dto;
    }

    public User toEntity(UserProfileDTO dto) {
        User user = new User();

        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setHeight(dto.getHeight());
        user.setWeight(dto.getWeight());
        user.setBodyType(dto.getBodyType());
        user.setLocation(dto.getLocation());
        user.setRelationshipType(dto.getRelationshipType());
        user.setAlcohol(dto.getAlcohol());
        user.setSmoke(dto.getSmoke());
        user.setDiet(dto.getDiet());
        user.setShorterBio(dto.getShorterBio());
        user.setHobbies(dto.getHobbies());
        user.setFavoriteMusic(dto.getFavoriteMusic());
        user.setWeekendPlans(dto.getWeekendPlans());

        return user;
    }

    public User toEntity(UserUpdateDTO dto) {
        User user = new User();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setHeight(dto.getHeight());
        user.setWeight(dto.getWeight());
        user.setBodyType(dto.getBodyType());
        user.setLocation(dto.getLocation());
        user.setRelationshipType(dto.getRelationshipType());
        user.setAlcohol(dto.getAlcohol());
        user.setSmoke(dto.getSmoke());
        user.setDiet(dto.getDiet());
        user.setShorterBio(dto.getShorterBio());
        user.setHobbies(dto.getHobbies());
        user.setFavoriteMusic(dto.getFavoriteMusic());
        user.setWeekendPlans(dto.getWeekendPlans());
        user.setAgePreference(dto.getAgePreference());
        user.setDistancePreference(dto.getDistancePreference());

        return user;
    }

    public User toEntity(UserCreateDTO dto) {
        User user = new User();

        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(dto.getGender());
        user.setHeight(dto.getHeight());
        user.setWeight(dto.getWeight());
        user.setBodyType(dto.getBodyType());
        user.setLocation(dto.getLocation());
        user.setRelationshipType(dto.getRelationshipType());
        user.setAlcohol(dto.getAlcohol());
        user.setSmoke(dto.getSmoke());
        user.setDiet(dto.getDiet());
        user.setShorterBio(dto.getShorterBio());
        user.setHobbies(dto.getHobbies());
        user.setFavoriteMusic(dto.getFavoriteMusic());
        user.setWeekendPlans(dto.getWeekendPlans());
        user.setAgePreference(dto.getAgePreference());
        user.setDistancePreference(dto.getDistancePreference());

        return user;
    }

    public User toEntity(UserAdminDTO dto) {
        User user = new User();

        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setApproved(dto.isApproved());
        user.setBanned(dto.isBanned());
        user.setConfirmed(dto.isConfirmed());
        user.setPersonalityScore(dto.getPersonalityScore());
        user.setRole(dto.getRole());
        user.setImages(
            dto.getImages() != null
                ? dto.getImages()
                    .stream()
                    .map(userImageMapper::toUserImageEntity)
                    .toList()
                : null
        );


        return user;
    }

}