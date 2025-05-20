package com.datingapp.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.datingapp.backend.dto.MessageDTO;
import com.datingapp.backend.service.MessageService;

@Controller
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public WebSocketMessageController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

        @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload MessageDTO message) {
        MessageDTO saved = messageService.saveMessage(message);

        // Hem alıcıya bildirim
        messagingTemplate.convertAndSend(
            "/topic/messages/" + message.getReceiverId(), saved);

        // Hem gönderene de kaydedilmiş mesajı yolla
        messagingTemplate.convertAndSend(
            "/topic/messages/" + message.getSenderId(), saved);
    }
}