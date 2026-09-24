package com.datingapp.backend.service;

public interface PushNotificationService {

    void send(String fcmToken, String title, String body, String relatedEntityId);
    
}