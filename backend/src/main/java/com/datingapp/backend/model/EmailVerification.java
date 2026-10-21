package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "email_verifications",
    uniqueConstraints = {@UniqueConstraint(name = "uk_email_verification_email", columnNames = "email")}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}