package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    public enum TokenType {PASSWORD_RESET}

    @Id
    private String hashToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private LocalDateTime expiryDate;

    public void setExpiryDate(LocalDateTime expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("The expiration date cannot be null.");
        }
        this.expiryDate = expiryDate;
    }
    
    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

}
