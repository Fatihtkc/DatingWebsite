package com.datingapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "managers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
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
    
    // İşe başlama tarihi
    private LocalDate startDate;
    
    // Rol bilgisi (örn: "ADMIN", "SUPERVISOR")
    private String role="manager";

    @Column(unique = true)
    private String phone;
    
    private String password; // Şifre, şifrelenmiş olarak saklanmalı
}
