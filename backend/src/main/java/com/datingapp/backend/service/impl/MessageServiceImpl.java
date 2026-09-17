package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.Message.MessageDTO;
import com.datingapp.backend.model.Message;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.MessageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MessageService;
import com.datingapp.backend.mapper.MessageMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDTO saveMessage(@AuthenticationPrincipal CustomUserDetails principal, MessageDTO dto) {

        Long userId = principal.getId();

        User sender = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
            .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(dto.getContent());
        msg.setSentAt(LocalDateTime.now());
        msg.setIsImage(dto.getIsImage());

        Message saved = messageRepository.save(msg);
        return messageMapper.toDTO(saved);
    }

    @Override
    public List<MessageDTO> getChatMessages(@AuthenticationPrincipal CustomUserDetails principal, Long user2Id) {

        Long user1Id = principal.getId();

        List<Message> msgs = messageRepository
            .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
                user1Id, user2Id, user2Id, user1Id);
        return msgs.stream().map(messageMapper::toDTO).collect(Collectors.toList());

    }
}
