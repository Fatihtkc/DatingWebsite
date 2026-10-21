package com.datingapp.backend.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.datingapp.backend.dto.Message.ConversationDTO;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.ConversationMapper;
import com.datingapp.backend.model.Conversation;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ConversationRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.ConversationCreatorService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConversationCreatorServiceImpl implements ConversationCreatorService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    @Override 
    @Transactional
    public ConversationDTO findOrCreate(Long user1Id, Long user2Id){
        return conversationRepository.findByUser1IdAndUser2Id(user1Id, user2Id).map(conversationMapper::toDTO)
            .orElseGet(() -> {
                User u1 = userRepository.findById(user1Id).orElseThrow(() -> new UserNotFoundException("User 1 not found"));
                User u2 = userRepository.findById(user2Id).orElseThrow(() -> new UserNotFoundException("User 2 not found"));

                Conversation c = new Conversation();
                c.setUser1(u1);
                c.setUser2(u2);
                return conversationMapper.toDTO(conversationRepository.saveAndFlush(c));
            });
    }

    @Override 
    @Transactional(readOnly = true)
    public Optional<ConversationDTO> find(Long user1Id, Long user2Id){
        return conversationRepository.findByUser1IdAndUser2Id(user1Id, user2Id).map(conversationMapper::toDTO);
    }
}