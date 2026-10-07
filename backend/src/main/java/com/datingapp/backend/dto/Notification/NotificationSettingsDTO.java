package com.datingapp.backend.dto.Notification;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationSettingsDTO {
    private boolean matchNotificationsEnabled;
    private boolean messageNotificationsEnabled;
    private boolean likeNotificationsEnabled;
}
