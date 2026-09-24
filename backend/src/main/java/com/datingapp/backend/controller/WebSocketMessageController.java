package com.datingapp.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.datingapp.backend.dto.Message.SendMessageDTO;
import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload SendMessageDTO message, @AuthenticationPrincipal CustomUserDetails principal) {
        
        GetMessageDTO saved = messageService.saveMessage(principal, message);

        messagingTemplate.convertAndSend("/topic/conversations/" + saved.getConversationId(),saved);
    }
}