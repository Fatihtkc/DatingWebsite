package com.datingapp.backend.service;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.dto.Message.SendMessageDTO;
import com.datingapp.backend.security.CustomUserDetails;

import java.util.List;

public interface MessageService {

    GetMessageDTO saveMessage(CustomUserDetails principal, SendMessageDTO dto);

    List<GetMessageDTO> getChatMessages(CustomUserDetails principal, Long conversationId);

}