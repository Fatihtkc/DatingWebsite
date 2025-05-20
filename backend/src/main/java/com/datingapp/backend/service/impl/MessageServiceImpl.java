package com.datingapp.backend.service.impl;

import com.datingapp.backend.dto.MessageDTO;
import com.datingapp.backend.model.Message;
import com.datingapp.backend.model.User;
import com.datingapp.backend.repository.MessageRepository;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.MessageService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;  // <<<

    public MessageServiceImpl(MessageRepository messageRepository,
                              UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MessageDTO saveMessage(MessageDTO dto) {
        User sender = userRepository.findById(dto.getSenderId())
            .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
            .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(dto.getContent());
        msg.setIsImage(dto.getIsImage());
        msg.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(msg);
        // DTO’ya manuel maple
        MessageDTO out = new MessageDTO();
        out.setId(saved.getId());
        out.setSenderId(saved.getSender().getId());
        out.setReceiverId(saved.getReceiver().getId());
        out.setContent(saved.getContent());
        out.setIsImage(saved.getIsImage());
        out.setSentAt(saved.getSentAt());
        return out;
    }

    // getChatMessages aynı kalabilir, ancak entity’lerde User referansları olduğu için:
    @Override
    public List<MessageDTO> getChatMessages(Long u1, Long u2) {
        List<Message> msgs = messageRepository
            .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySentAtAsc(
                u1, u2, u2, u1);
        return msgs.stream().map(m -> {
            MessageDTO d = new MessageDTO();
            d.setId(m.getId());
            d.setSenderId(m.getSender().getId());
            d.setReceiverId(m.getReceiver().getId());
            d.setContent(m.getContent());
            d.setIsImage(m.getIsImage());
            d.setSentAt(m.getSentAt());
            return d;
        }).collect(Collectors.toList());
    }
}
