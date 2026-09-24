package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.NotificationDTO;
import com.datingapp.backend.dto.NotificationSettingsDTO;
import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.mapper.NotificationMapper;
import com.datingapp.backend.model.Notification;
import com.datingapp.backend.model.NotificationSettings;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.NotificationRepository;
import com.datingapp.backend.repository.NotificationSettingsRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.NotificationService;
import com.datingapp.backend.service.PushNotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor 
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository settingsRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushService;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void notify(User recipient, NotificationType type, Long relatedEntityId, String title, String body, boolean sendPush) {

        Optional<NotificationSettings> settingsOpt = settingsRepository.findByUserId(recipient.getId());

        if (settingsOpt.isPresent() && !isEnabled(settingsOpt.get(), type)) {
            return;
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setType(type);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        if (sendPush) {
            settingsOpt.ifPresent(settings ->
                pushService.send(settings.getFcmToken(), title, body,
                    relatedEntityId != null ? relatedEntityId.toString() : null)
            );
        }
    }

        @Override
    public void updateFcmToken(Long userId, String token) {

        NotificationSettings settings =
                settingsRepository.findByUserId(userId)
                        .orElseGet(() -> {

                            NotificationSettings newSettings =
                                    new NotificationSettings();

                            newSettings.setUser(
                                    userRepository.getReferenceById(userId)
                            );

                            return newSettings;
                        });

        settings.setFcmToken(token);

        settingsRepository.save(settings);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getNotifications(Long userId, Pageable pageable){

        Page<Notification> notifications =
                notificationRepository
                        .findByRecipientIdOrderByCreatedAtDesc(
                                userId,
                                pageable
                        );

        return notifications.map(notificationMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId){

        return notificationRepository
                .countByRecipientIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId){

        notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void updateSettings(Long userId, NotificationSettingsDTO dto){

        NotificationSettings settings = settingsRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Notification settings not found"));

        settings.setMatchNotificationsEnabled(dto.isMatchNotificationsEnabled());
        settings.setMessageNotificationsEnabled(dto.isMessageNotificationsEnabled());
        settings.setLikeNotificationsEnabled(dto.isLikeNotificationsEnabled());

        settingsRepository.save(settings);
    }

    private boolean isEnabled(NotificationSettings settings, NotificationType type) {
        return switch (type) {
            case NEW_MATCH -> settings.isMatchNotificationsEnabled();
            case NEW_MESSAGE -> settings.isMessageNotificationsEnabled();
            case NEW_LIKE -> settings.isLikeNotificationsEnabled();
        };
    }
}