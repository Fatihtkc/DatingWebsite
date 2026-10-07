package com.datingapp.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.datingapp.backend.enums.OtpPurpose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "otp_codes",
    uniqueConstraints = { 
        @UniqueConstraint(name = "uk_otp_email_purpose", columnNames = {"email", "purpose"})
    }
)
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 64)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OtpPurpose purpose;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private LocalDateTime expiry;
}