package com.datingapp.backend.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.records.MessageSavedEvent;
import com.datingapp.backend.repository.UserRepository;
import com.datingapp.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageNotificationListener {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MessageSavedEvent e){
        try{
            userRepository.findById(e.receiverId()).ifPresent(receiver -> 
                notificationService.notify(receiver, NotificationType.NEW_MESSAGE,
                    e.messageId(), e.senderName(), e.preview(), e.push()));
        }catch (Exception ex){
            log.warn("Notification failed for message {}", e.messageId(), ex);
        }
    }
}