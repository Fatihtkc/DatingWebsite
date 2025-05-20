package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    public enum TokenType {
        PASSWORD_RESET
    }

    @Id
    private String token;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    private LocalDateTime expiryDate;

    public void setExpiryDate(LocalDateTime expiryDate) {
        if (expiryDate == null) {
            throw new IllegalArgumentException("Son kullanma tarihi null olamaz");
        }
        this.expiryDate = expiryDate;
    }
    
    // İsteğe bağlı olarak expiryDate değerini almak için bir getter metodu ekleyebilirsiniz
    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

}
