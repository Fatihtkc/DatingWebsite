package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "matches",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user1_id", "user2_id"})},
    indexes = { 
        @Index(name = "idx_match_user1", columnList = "user1_id"), 
        @Index(name = "idx_match_user2", columnList = "user2_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;
    
    private LocalDateTime matchedAt;
}
