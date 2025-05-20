package com.datingapp.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // "/topic" ile başlayarak mesajları abone olanlara yönlendiriyoruz
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");  // "/app" ile başlayan mesajlar server'a yönlendirilir
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint'ini "/chat" olarak tanımlıyoruz ve SockJS desteği ekliyoruz
        registry.addEndpoint("/chat")
                .setAllowedOrigins("http://localhost:3000")  // Burada React frontend'inizin URL'sini belirtiyoruz
                .withSockJS();  // SockJS fallback destek sağlar
    }
}
