package com.datingapp.backend.websocket;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class WebSocketPresenceRegistry {

    private final ConcurrentHashMap<Long, Set<String>> onlineUsers = new ConcurrentHashMap<>();

    public void userConnected(Long userId, String sessionId) {
        onlineUsers.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
    }

    public void userDisconnected(Long userId, String sessionId) {
        Set<String> sessions = onlineUsers.get(userId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                onlineUsers.remove(userId);
            }
        }
    }

    public boolean isOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }
}