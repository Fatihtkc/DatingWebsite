package com.datingapp.backend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.datingapp.backend.enums.NotificationType;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private Long Id;
    private NotificationType Type;
    private String Title;
    private String Body;
    private Long RelatedEntityId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
