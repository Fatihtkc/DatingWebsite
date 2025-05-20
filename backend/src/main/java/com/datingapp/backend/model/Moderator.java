package com.datingapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

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
    private String fullName;

    private String imageUrl;

    @Column(unique = true)
    @Email(message = "Geçerli bir email giriniz")
    private String email;
    
    private LocalDate birthDate;
    private LocalDate startDate;

    @Column(unique = true)
    private String phone;
    
    private String password; // Şifre, şifrelenmiş olarak saklanmalı

    private String role="moderator";

}
