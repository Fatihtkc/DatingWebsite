package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "refresh_tokens",
    indexes = {
        @Index(name = "uk_refresh_token_hash", columnList = "tokenHash", unique = true),
        @Index(name = "idx_refresh_token_subject", columnList = "subject"),
        @Index(name = "idx_refresh_token_family", columnList = "familyId"),
        @Index(name = "idx_refresh_token_expiry", columnList = "expiryDate")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;      

    @Column(nullable = false)
    private String subject;        

    @Column(nullable = false)
    private String familyId;       

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}