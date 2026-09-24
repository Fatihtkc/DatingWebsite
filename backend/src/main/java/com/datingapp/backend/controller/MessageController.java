package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{conversationId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<Page<GetMessageDTO>> getChatMessages(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long conversationId, 
            @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(messageService.getChatMessages(principal, conversationId, pageable));
    }
}

