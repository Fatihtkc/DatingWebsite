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

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserUpdateDTO {

    @Size(min = 3, max = 30)
    private String username;

    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    @Past
    private LocalDate birthDate;

    private Gender gender;

    @DecimalMin(value = "50.0")
    @DecimalMax(value = "250.0")
    private Double height;

    @Size(max = 500)
    private String location;

    @Size(max = 500)
    private String shorterBio;

    @Size(max = 1000)
    private String hobbies;

    @Size(max = 200)
    private String favoriteMusic;

    @Size(max = 500)
    private String weekendPlans;

    private BodyType bodyType;

    private RelationshipType relationshipType;

    @Size(max = 500)
    private String agePreference;
    
    @Size(max = 500)
    private String distancePreference;

    private Smoking smoke;

    private Alcohol alcohol;
    
    private Diet diet;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;
}