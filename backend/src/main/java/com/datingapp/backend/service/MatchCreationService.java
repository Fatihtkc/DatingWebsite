package com.datingapp.backend.service;

import com.datingapp.backend.model.Match;

public interface MatchCreationService{

    Match createMatchRecord(Match match);
}