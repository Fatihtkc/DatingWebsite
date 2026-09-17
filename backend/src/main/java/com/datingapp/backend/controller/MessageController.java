package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Message.MessageDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{user1}/{user2}")
    @PreAuthorize("#user1 == principal.id or #user2 == principal.id")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long user2) {
        return ResponseEntity.ok(messageService.getChatMessages(principal, user2));
    }
}

