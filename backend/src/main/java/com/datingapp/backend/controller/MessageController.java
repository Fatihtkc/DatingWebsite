package com.datingapp.backend.controller;

import com.datingapp.backend.dto.MessageDTO;
import com.datingapp.backend.service.MessageService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable Long user1, @PathVariable Long user2) {
        return ResponseEntity.ok(messageService.getChatMessages(user1, user2));
    }
}

