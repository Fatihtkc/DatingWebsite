package com.datingapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import com.datingapp.backend.enums.Role;


@Entity
@Table(name = "moderators")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moderator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Kişisel bilgiler
    private String firstName;
    private String lastName;

    private String imageUrl;

    @Column(nullable = false, unique = true)
    @Email(message = "Geçerli bir email giriniz")
    private String email;
    
    private LocalDate birthDate;
    
    private LocalDate startDate;

    @Column(nullable = false, unique = true)
    private String phone;
    
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role=Role.MODERATOR;

}
