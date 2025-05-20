package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // İlk kullanıcı
    @ManyToOne
    @JoinColumn(name = "user1_id")
    private User user1;
    
    // İkinci kullanıcı
    @ManyToOne
    @JoinColumn(name = "user2_id")
    private User user2;
    
    private LocalDateTime matchedAt;
}
