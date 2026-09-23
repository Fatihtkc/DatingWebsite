package com.datingapp.backend.mapper;

import org.springframework.stereotype.Component;

import com.datingapp.backend.dto.Message.ConversationDTO;
import com.datingapp.backend.model.Conversation;

@Component
public class ConversationMapper {

    public ConversationDTO toDTO(Conversation conversation) {

        ConversationDTO dto = new ConversationDTO();

        dto.setId(conversation.getId());
        dto.setUser1Id(conversation.getUser1().getId());
        dto.setUser2Id(conversation.getUser2().getId());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setLastMessageAt(conversation.getLastMessageAt());
        dto.setUser1HasUnread(conversation.getUser1HasUnread());
        dto.setUser2HasUnread(conversation.getUser2HasUnread());

        return dto;
    }
}