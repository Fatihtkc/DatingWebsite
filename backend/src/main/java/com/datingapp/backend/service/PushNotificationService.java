package com.datingapp.backend.service;

public interface PushNotificationService {

    void send(Long userId,String fcmToken, String title, String body, String relatedEntityId);
    
}