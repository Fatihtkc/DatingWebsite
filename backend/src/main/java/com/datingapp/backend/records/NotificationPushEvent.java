package com.datingapp.backend.records;

public record NotificationPushEvent(Long userId, String fcmToken, String title, String body, String relatedEntityId){
}