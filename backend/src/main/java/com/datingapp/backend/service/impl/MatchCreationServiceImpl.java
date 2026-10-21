package com.datingapp.backend.service.impl;

import com.datingapp.backend.model.Match;
import com.datingapp.backend.records.MatchCreatedEvent;
import com.datingapp.backend.repository.MatchRepository;
import com.datingapp.backend.service.MatchCreationService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchCreationServiceImpl implements MatchCreationService{

    private final MatchRepository matchRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Match createMatchRecord(Match match){
        Match saved = matchRepository.saveAndFlush(match);

        eventPublisher.publishEvent(new MatchCreatedEvent(saved.getId()));

        return saved;
    }
}