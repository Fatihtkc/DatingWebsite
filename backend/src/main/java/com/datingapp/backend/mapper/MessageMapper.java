package com.datingapp.backend.mapper;

import com.datingapp.backend.dto.Message.MessageDTO;
import com.datingapp.backend.model.Message;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    
    public MessageDTO toDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setIsImage(message.getIsImage());
        return dto;
    }

}
