package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Message.ConversationDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ConversationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/{userId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<Long> createOrGetConversation(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long userId) {

        ConversationDTO conversation = conversationService.getOrCreateConversation( principal.getId(), userId);

        return ResponseEntity.ok(conversation.getId());
    }
}