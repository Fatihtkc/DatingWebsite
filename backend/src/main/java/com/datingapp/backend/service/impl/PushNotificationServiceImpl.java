package com.datingapp.backend.service.impl;

import com.datingapp.backend.service.NotificationService;
import com.datingapp.backend.service.PushNotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class PushNotificationServiceImpl implements PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

    private final NotificationService notificationService;

    @Override
    public void send(Long userId, String fcmToken, String title, String body, String relatedEntityId){

        if (fcmToken == null || fcmToken.isBlank()){
            return;
        }

        Message message = Message.builder()
            .setToken(fcmToken)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .putData("relatedEntityId", relatedEntityId != null ? relatedEntityId : "")
            .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (isInvalidToken(e)){

                notificationService.clearFcmToken(userId);

                log.warn("Removed invalid FCM token for user {}",userId);

            } else{

                log.warn("FCM notification failed for user {}: {}",userId,e.getMessage());
            }
        }
    }
    
    private boolean isInvalidToken(FirebaseMessagingException e){
        MessagingErrorCode code = e.getMessagingErrorCode();

        return code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.SENDER_ID_MISMATCH;
    }
}