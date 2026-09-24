package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Conversation;
import com.datingapp.backend.dto.Message.ConversationDTO;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ConversationRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ConversationService;
import com.datingapp.backend.service.MatchService;
import com.datingapp.backend.mapper.ConversationMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;
    private final MatchService matchService;

    @Override
    public ConversationDTO getOrCreateConversation(Long userAId, Long userBId) {

        Long user1Id = Math.min(userAId, userBId);
        Long user2Id = Math.max(userAId, userBId);

        if (!matchService.isMatched(user1Id, user2Id)){
            throw new IllegalArgumentException(
                    "Users are not matched"
        );}

        Conversation conversation = conversationRepository
                .findByUser1IdAndUser2Id(user1Id, user2Id)
                .orElseGet(() -> {

                    User user1 = userRepository.findById(user1Id)
                            .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));

                    User user2 = userRepository.findById(user2Id)
                            .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

                    Conversation newConversation = new Conversation();

                    newConversation.setUser1(user1);
                    newConversation.setUser2(user2);

                    return conversationRepository.save(newConversation);
                });

        return conversationMapper.toDTO(conversation);
    }

    @Override
    public Conversation getConversationForUser(Long conversationId, Long userId) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!conversation.getUser1().getId().equals(userId)
                && !conversation.getUser2().getId().equals(userId)) {

            throw new IllegalArgumentException("User is not part of this conversation");
        }

        return conversation;
    }

}