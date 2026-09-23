package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.model.Message;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    
    public GetMessageDTO toDTO(Message message) {
        GetMessageDTO dto = new GetMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setIsImage(message.getIsImage());
        dto.setIsRead(message.getIsRead());
        return dto;
    }

}
