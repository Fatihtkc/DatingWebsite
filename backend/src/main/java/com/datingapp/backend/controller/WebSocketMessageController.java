package com.datingapp.backend.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.datingapp.backend.dto.Message.MessageDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

        @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload MessageDTO message, @AuthenticationPrincipal CustomUserDetails principal) {
        MessageDTO saved = messageService.saveMessage(principal, message);

        // Hem alıcıya bildirim
        messagingTemplate.convertAndSend(
            "/topic/messages/" + message.getReceiverId(), saved);

        // Hem gönderene de kaydedilmiş mesajı yolla
        messagingTemplate.convertAndSend(
            "/topic/messages/" + principal, saved);
    }
}