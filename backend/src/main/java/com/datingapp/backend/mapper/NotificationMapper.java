package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.NotificationDTO;
import com.datingapp.backend.dto.NotificationSettingsDTO;
import com.datingapp.backend.model.Notification;
import com.datingapp.backend.model.NotificationSettings;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification){

        NotificationDTO dto = new NotificationDTO();

        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitle(notification.getTitle());
        dto.setBody(notification.getBody());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }

    public NotificationSettingsDTO toDTO(NotificationSettings notificationSettings){

        NotificationSettingsDTO dto = new NotificationSettingsDTO();

        dto.setLikeNotificationsEnabled(notificationSettings.isLikeNotificationsEnabled());
        dto.setMatchNotificationsEnabled(notificationSettings.isMatchNotificationsEnabled());
        dto.setMessageNotificationsEnabled(notificationSettings.isMessageNotificationsEnabled());

        return dto;
    }

    
}
