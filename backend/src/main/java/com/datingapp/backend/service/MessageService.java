package com.datingapp.backend.service;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.dto.Message.SendMessageDTO;
import com.datingapp.backend.security.CustomUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    GetMessageDTO saveMessage(CustomUserDetails principal, SendMessageDTO dto);

    Page<GetMessageDTO> getChatMessages(CustomUserDetails principal, Long conversationId, Pageable pageable);

}