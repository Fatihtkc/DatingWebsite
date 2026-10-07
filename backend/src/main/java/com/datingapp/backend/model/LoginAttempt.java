package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "login_attempts", 
    indexes = {
        @Index(name = "idx_login_attempt_identifier_success_time", columnList = "identifier, successful, attempted_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String identifierType;

    @Column(nullable = false)
    private LocalDateTime attemptedAt;

    @Column(nullable = false)
    private boolean successful;
}