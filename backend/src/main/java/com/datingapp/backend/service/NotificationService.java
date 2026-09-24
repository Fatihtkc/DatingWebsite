package com.datingapp.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.datingapp.backend.dto.NotificationDTO;
import com.datingapp.backend.dto.NotificationSettingsDTO;
import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.model.User;

public interface NotificationService {

    void notify(User recipient, NotificationType type, Long relatedEntityId, String title, String body, boolean sendPush);
    void updateFcmToken(Long userId, String token);
    Page<NotificationDTO> getNotifications(Long userId, Pageable pageable);
    long getUnreadCount(Long userId);
    void markAllAsRead(Long userId);
    void updateSettings(Long userId, NotificationSettingsDTO dto);
}