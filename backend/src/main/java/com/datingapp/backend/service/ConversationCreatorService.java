package com.datingapp.backend.service;

import java.util.Optional;

import com.datingapp.backend.dto.Message.ConversationDTO;

public interface ConversationCreatorService {

    ConversationDTO findOrCreate(Long user1Id, Long user2Id);

    Optional<ConversationDTO> find(Long user1Id, Long user2Id);
}