package com.datingapp.backend.dto.Notification;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.datingapp.backend.enums.NotificationType;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private Long relatedEntityId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
