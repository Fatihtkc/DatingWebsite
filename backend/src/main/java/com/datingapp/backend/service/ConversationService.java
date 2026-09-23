package com.datingapp.backend.service;

import com.datingapp.backend.dto.Message.ConversationDTO;
import com.datingapp.backend.model.Conversation;

public interface ConversationService {

    ConversationDTO getOrCreateConversation(Long userAId, Long userBId);

    ConversationDTO getConversationForUser(Long conversationId, Long userId);

    Conversation getConversationEntityForUser(Long conversationId, Long userId);

}