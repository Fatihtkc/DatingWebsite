package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.dto.Message.SendMessageDTO;
import com.datingapp.backend.exception.UserNotFoundException;
import com.datingapp.backend.mapper.MessageMapper;
import com.datingapp.backend.model.Conversation;
import com.datingapp.backend.model.Message;
import com.datingapp.backend.model.User;
import com.datingapp.backend.records.MessageSavedEvent;
import com.datingapp.backend.repository.ConversationRepository;
import com.datingapp.backend.repository.MessageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ConversationService;
import com.datingapp.backend.service.MatchService;
import com.datingapp.backend.service.UserBlockService;
import com.datingapp.backend.websocket.WebSocketPresenceRegistry;

import org.springframework.transaction.annotation.Transactional;

import com.datingapp.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MatchService matchService;
    private final ConversationService conversationService;
    private final UserBlockService userBlockService;
    private final MessageMapper messageMapper;
    private final WebSocketPresenceRegistry presenceRegistry;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional 
    public GetMessageDTO saveMessage(CustomUserDetails principal, SendMessageDTO dto){

        Long senderId = principal.getId();

        User sender = userRepository.findById(senderId).orElseThrow(() -> new UserNotFoundException(senderId));

        if(sender.isBanned()){
            throw new AccessDeniedException("Account is banned.");
        }

        Conversation conversation = conversationService.getConversationForUser(dto.getConversationId(), senderId);

        Long otherUserId;

        if(conversation.getUser1().getId().equals(senderId)){
            otherUserId = conversation.getUser2().getId();
        }else{
            otherUserId = conversation.getUser1().getId();
        }

        if(userBlockService.isBlockedBetween(senderId, otherUserId)){
            throw new AccessDeniedException("You cannot send messages to this user.");
        }

        if(!matchService.isMatched(senderId, otherUserId)){
            throw new IllegalArgumentException("Users are not matched");
        }

        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(dto.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setIsImage(dto.getIsImage() != null ? dto.getIsImage() : false);
        message.setIsRead(false);

        Message saved = messageRepository.save(message);

        conversation.setLastMessageAt(saved.getSentAt());
        conversationRepository.save(conversation);

        boolean receiverOnline = presenceRegistry.isOnline(otherUserId);

        String preview = Boolean.TRUE.equals(saved.getIsImage()) ? "Sent a photo" : saved.getContent();

        eventPublisher.publishEvent(new MessageSavedEvent(otherUserId, saved.getId(),
            sender.getFirstName() + " " + sender.getLastName(), preview, !receiverOnline));

        return messageMapper.toDTO(saved);
    }

    @Override
    public Page<GetMessageDTO> getChatMessages(CustomUserDetails principal, Long conversationId, Pageable pageable){

        Long userId = principal.getId();

        conversationService.getConversationForUser(conversationId, userId);

        return messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable).map(messageMapper::toDTO);
    }
}