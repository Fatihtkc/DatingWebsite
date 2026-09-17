package com.datingapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import com.datingapp.backend.enums.Alcohol;
import com.datingapp.backend.enums.BodyType;
import com.datingapp.backend.enums.Diet;
import com.datingapp.backend.enums.Gender;
import com.datingapp.backend.enums.RelationshipType;
import com.datingapp.backend.enums.Role;
import com.datingapp.backend.enums.Smoking;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Authentication
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    @Email(message = "Geçerli bir email giriniz")
    private String email;
    
    @Column(nullable = false)
    private String password;

    //Personal Information
    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    private LocalDate birthDate;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Double height;

    private Double weight;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @Enumerated(EnumType.STRING)
    private Diet diet;

    private String hobbies;

    private String favoriteMusic;

    private String weekendPlans;

    private String location;

    @Lob
    private String shorterBio;
    
    //Preferences
    @Column(name = "relationship_type")
    @Enumerated(EnumType.STRING)
    private RelationshipType relationshipType;

    private String agePreference;

    private String distancePreference;
    
    //Habits
    @Enumerated(EnumType.STRING)
    private Smoking smoke;

    @Enumerated(EnumType.STRING)
    private Alcohol alcohol;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(name = "location_point", columnDefinition = "POINT SRID 4326")
    private Point locationPoint;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserImage> images;

    //Moderation
    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false)
    private boolean banned = false;

    @Column(nullable = false)
    private boolean confirmed = false;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private int personalityScore;

}
