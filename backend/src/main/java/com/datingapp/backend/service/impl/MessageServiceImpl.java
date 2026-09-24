package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Message.GetMessageDTO;
import com.datingapp.backend.dto.Message.SendMessageDTO;
import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.mapper.MessageMapper;
import com.datingapp.backend.model.Conversation;
import com.datingapp.backend.model.Message;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.ConversationRepository;
import com.datingapp.backend.repository.MessageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.ConversationService;
import com.datingapp.backend.service.MatchService;
import com.datingapp.backend.service.UserBlockService;
import com.datingapp.backend.websocket.WebSocketPresenceRegistry;
import com.datingapp.backend.service.MessageService;
import com.datingapp.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    private final NotificationService notificationService;
    private final WebSocketPresenceRegistry presenceRegistry;

    @Override
    public GetMessageDTO saveMessage(CustomUserDetails principal, SendMessageDTO dto) {

        Long senderId = principal.getId();

        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Conversation conversation = conversationService.getConversationForUser(dto.getConversationId(), senderId);

        Long otherUserId;

        if (conversation.getUser1().getId().equals(senderId)) {
            otherUserId = conversation.getUser2().getId();
        } else {
            otherUserId = conversation.getUser1().getId();
        }

        if (userBlockService.isBlockedBetween(senderId, otherUserId)) {
            throw new AccessDeniedException("You cannot send messages to this user.");
        }

        if (!matchService.isMatched(senderId, otherUserId)) {
            throw new IllegalArgumentException(
                    "Users are not matched");
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

        User receiverUser = userRepository.findById(otherUserId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        notificationService.notify(receiverUser, NotificationType.NEW_MESSAGE, saved.getId(), sender.getFirstName() + " " + sender.getLastName(), 
            Boolean.TRUE.equals(saved.getIsImage()) ? "Sent a photo" : saved.getContent(), !receiverOnline);

        return messageMapper.toDTO(saved);
    }

    @Override
    public Page<GetMessageDTO> getChatMessages(CustomUserDetails principal, Long conversationId, Pageable pageable) {

        Long userId = principal.getId();

        conversationService.getConversationForUser(conversationId, userId);

        return messageRepository
                .findByConversationIdOrderBySentAtDesc(conversationId, pageable)
                .map(messageMapper::toDTO);
    }
}