package com.datingapp.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "notification_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettings {

    @Id
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String fcmToken;

    @Column(nullable = false)
    private boolean matchNotificationsEnabled = true;

    @Column(nullable = false)
    private boolean messageNotificationsEnabled = true;

    @Column(nullable = false)
    private boolean likeNotificationsEnabled = true;
}