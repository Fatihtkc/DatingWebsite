package com.datingapp.backend.service;

import com.datingapp.backend.dto.MessageDTO;
import java.util.List;

public interface MessageService {
    MessageDTO saveMessage(MessageDTO messageDTO);
    List<MessageDTO> getChatMessages(Long user1, Long user2);
}
