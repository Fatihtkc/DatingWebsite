package com.datingapp.backend.service.impl;

import com.datingapp.backend.service.PushNotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationServiceImpl implements PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    @Override
    public void send(String fcmToken, String title, String body, String relatedEntityId) {
        if (fcmToken == null || fcmToken.isBlank()) {
            return;
        }

        Message message = Message.builder()
            .setToken(fcmToken)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putData("relatedEntityId", relatedEntityId != null ? relatedEntityId : "")
            .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.warn("Push notification could not be sent, token: {}, error: {}", fcmToken, e.getMessage());
        }
    }
}