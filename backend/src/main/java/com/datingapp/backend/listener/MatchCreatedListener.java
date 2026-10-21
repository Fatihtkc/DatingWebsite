package com.datingapp.backend.listener;

import com.datingapp.backend.enums.NotificationType;
import com.datingapp.backend.model.Match;
import com.datingapp.backend.records.MatchCreatedEvent;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchCreatedListener {

    private final MatchRepository matchRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMatchCreated(MatchCreatedEvent event){

        Match match = matchRepository.findById(event.matchId()).orElse(null);

        if(match == null){
            log.warn("Committed match not found: {}", event.matchId());
            return;
        }

        notificationService.notify(
                match.getUser1(),
                NotificationType.NEW_MATCH,
                match.getId(),
                "New Match! 🎉",
                "You matched with " + match.getUser2().getFirstName() + " " + match.getUser2().getLastName() + "!",
                true
        );

        notificationService.notify(
                match.getUser2(),
                NotificationType.NEW_MATCH,
                match.getId(),
                "New Match! 🎉",
                "You matched with " + match.getUser1().getFirstName() + " " + match.getUser1().getLastName() + "!",
                true
        );
    }
}