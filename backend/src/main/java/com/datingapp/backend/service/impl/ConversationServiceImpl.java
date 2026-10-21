package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Conversation;
import com.datingapp.backend.dto.Message.ConversationDTO;
import com.datingapp.backend.repository.ConversationRepository;
import com.datingapp.backend.service.ConversationCreatorService;
import com.datingapp.backend.service.ConversationService;
import com.datingapp.backend.service.MatchService;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MatchService matchService;
    private final ConversationCreatorService creator;


    @Override
    @Transactional
    public ConversationDTO getOrCreateConversation(Long userAId, Long userBId){

        Long user1Id = Math.min(userAId, userBId);
        Long user2Id = Math.max(userAId, userBId);

        if(!matchService.isMatched(user1Id, user2Id)){
            throw new IllegalArgumentException("Users are not matched");
        }

        try {
            return creator.findOrCreate(user1Id, user2Id);
        } catch (DataIntegrityViolationException e) {
            return creator.find(user1Id, user2Id).orElseThrow(() -> e);
        }
    }

    @Override
    public Conversation getConversationForUser(Long conversationId, Long userId){

        Conversation conversation = conversationRepository
            .findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if(!conversation.getUser1().getId().equals(userId) && !conversation.getUser2().getId().equals(userId)){
            throw new IllegalArgumentException("User is not part of this conversation");
        }

        return conversation;
    }

}