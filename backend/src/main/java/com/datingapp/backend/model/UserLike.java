package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "user_likes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_like", columnNames = {"liker_id", "liked_id"})
    },
    indexes = {
        @Index(name = "idx_user_like_liker", columnList = "liker_id"),
        @Index(name = "idx_user_like_liked", columnList = "liked_id"),
        @Index(name = "idx_user_like_liker_liked", columnList = "liker_id, liked_id", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liker_id")
    private User liker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liked_id")
    private User liked;

    private LocalDateTime likedAt;
}
