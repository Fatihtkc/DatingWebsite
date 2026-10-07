package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Stats.UserStatsDTO;
import com.datingapp.backend.service.UserStatsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class UserStatsController{

    private final UserStatsService userStatsService;

    @GetMapping
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<UserStatsDTO> getStats(){
        return ResponseEntity.ok(userStatsService.getStats());
    }
}