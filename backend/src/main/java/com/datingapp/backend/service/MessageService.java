package com.datingapp.backend.service;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.datingapp.backend.dto.Message.MessageDTO;
import com.datingapp.backend.security.CustomUserDetails;

public interface MessageService {
    MessageDTO saveMessage(@AuthenticationPrincipal CustomUserDetails principal, MessageDTO messageDTO);
    List<MessageDTO> getChatMessages(@AuthenticationPrincipal CustomUserDetails principal, Long user2);
}
