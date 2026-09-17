package com.datingapp.backend.dto.User;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import com.datingapp.backend.enums.Alcohol;
import com.datingapp.backend.enums.BodyType;
import com.datingapp.backend.enums.Diet;
import com.datingapp.backend.enums.Gender;
import com.datingapp.backend.enums.RelationshipType;
import com.datingapp.backend.enums.Smoking;

@Data
@NoArgsConstructor
public class UserCreateDTO {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private LocalDate birthDate;
    private Gender gender;

    private Double height;
    private Double weight;
    private BodyType bodyType;

    private String location;

    private RelationshipType relationshipType;

    private String agePreference;
    private String distancePreference;

    private Smoking smoke;
    private Alcohol alcohol;
    private Diet diet;

    private String shorterBio;

    private String hobbies;
    private String favoriteMusic;
    private String weekendPlans;
}