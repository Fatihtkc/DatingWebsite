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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserCreateDTO{

    @NotBlank
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(min = 12, max = 128)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{12,128}$",
        message = "The password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String password;

    @Past
    private LocalDate birthDate;

    @NotBlank
    private Gender gender;

    @DecimalMin(value = "50.0")
    @DecimalMax(value = "250.0")
    private Double height;

    @DecimalMin(value = "20.0")
    @DecimalMax(value = "300.0")
    private Double weight;

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

    @Size(max = 500)
    private BodyType bodyType;

    @NotBlank
    @Size(max = 500)
    private RelationshipType relationshipType;

    @Size(max = 500)
    private String agePreference;
    
    @Size(max = 500)
    private String distancePreference;

    @Size(max = 500)
    private Smoking smoke;

    @Size(max = 500)
    private Alcohol alcohol;

    @Size(max = 500)
    private Diet diet;
}