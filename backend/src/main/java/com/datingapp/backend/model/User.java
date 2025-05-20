package com.datingapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kişisel bilgiler
    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(length = 120)
    private String fullName;
    private LocalDate birthDate;
    private String gender;

    @Column(nullable = false, unique = true)
    @Email(message = "Geçerli bir email giriniz")
    private String email;
    
    @Column(nullable = false)
    private String password;

    // Fiziksel özellikler
    private Double height;
    private Double weight;
    private String bodyType;
    
    // Konum bilgisi (basitçe string olarak, detaylandırılabilir)
    private String location;
    
    // İlişki tercihi ve aranan kriterler
    @Column(name = "relationship_type")
    private String relationshipType;

    private String agePreference;    // örn: "25-35"
    private String distancePreference; // örn: "50km"
    
    // Alışkanlıklar
    private String smoke;
    private String alcohol;
    
    // Hakkında kısmı
    @Lob
    private String shorterbio;
    
    // Kullanıcının yüklediği resimler
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserImage> images;

    // Moderatör onayı için
    @Column(nullable = false)
    private boolean approved = false;

    // Banlama durumu için
    @Column(nullable = false)
    private boolean banned = false;

    @Column(nullable = false)
    private boolean confirmed = false;

    private int personalityScore;

    private String role="user";

    private String diet;
    private String hobbies;
    private String favoriteMusic;
    private String weekendPlans;

    private double latitude;
    private double longitude;
}
