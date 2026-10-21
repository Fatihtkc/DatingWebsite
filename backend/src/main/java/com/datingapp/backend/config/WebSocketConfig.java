package com.datingapp.backend.config;

import com.datingapp.backend.websocket.StompAuthChannelInterceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor authInterceptor;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigin;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/chat").setAllowedOrigins(allowedOrigin).withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(authInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration){
        registration
            .setMessageSizeLimit(16 * 1024)       
            .setSendBufferSizeLimit(512 * 1024)    
            .setSendTimeLimit(20 * 1000)           
            .setTimeToFirstMessage(30 * 1000);     
    }
}