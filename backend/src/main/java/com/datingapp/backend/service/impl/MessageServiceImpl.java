package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.dto.Message.SendMessageDTO;
import com.datingapp.backend.mapper.MessageMapper;
import com.datingapp.backend.model.Conversation;
import com.datingapp.backend.model.Message;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.MessageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ConversationService;
import com.datingapp.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationService conversationService;
    private final MessageMapper messageMapper;

    @Override
    public GetMessageDTO saveMessage(CustomUserDetails principal, SendMessageDTO dto) {

        Long senderId = principal.getId();

        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Conversation conversation = conversationService.getConversationForUser(dto.getConversationId(), senderId);

        Message message = new Message();

        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(dto.getContent());
        message.setSentAt(LocalDateTime.now());

        message.setIsImage(dto.getIsImage() != null ? dto.getIsImage() : false);

        message.setIsRead(false);

        Message saved = messageRepository.save(message);

        return messageMapper.toDTO(saved);
    }

    @Override
    public List<GetMessageDTO> getChatMessages(CustomUserDetails principal, Long conversationId) {

        Long userId = principal.getId();

        conversationService.getConversationForUser(conversationId, userId);

        return messageRepository
                .findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(messageMapper::toDTO)
                .toList();
    }
}