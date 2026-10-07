package com.datingapp.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import com.datingapp.backend.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "managers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;

    private String imageUrl;

    @Column(nullable = false, unique = true)
    @Email(message = "Geçerli bir email giriniz")
    private String email;
    
    private LocalDate birthDate;
    
    private LocalDate startDate;
    
    @Enumerated(EnumType.STRING)
    private Role role=Role.MANAGER;

    @Column(nullable = false, unique = true)
    private String phone;

    @JsonIgnore
    private String password;
}
