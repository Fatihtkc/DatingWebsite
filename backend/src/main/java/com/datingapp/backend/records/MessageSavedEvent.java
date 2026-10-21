package com.datingapp.backend.records;

public record MessageSavedEvent(Long receiverId, Long messageId, String senderName, String preview, boolean push){
    
}