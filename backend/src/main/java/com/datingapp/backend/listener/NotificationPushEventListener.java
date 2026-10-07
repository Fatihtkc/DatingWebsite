package com.datingapp.backend.listener;

import com.datingapp.backend.records.NotificationPushEvent;
import com.datingapp.backend.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationPushEventListener {

    private final PushNotificationService pushService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationPushEvent event) {

        if (event.fcmToken() == null || event.fcmToken().isBlank()){
            return;
        }

        pushService.send(event.userId(), event.fcmToken(), event.title(), event.body(),event.relatedEntityId());
    }
}