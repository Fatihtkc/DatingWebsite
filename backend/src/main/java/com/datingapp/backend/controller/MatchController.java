package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.model.Match;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MatchService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<Page<MatchDTO>> getMyMatches(@AuthenticationPrincipal CustomUserDetails principal, @PageableDefault(page = 0, size = 20) Pageable pageable){

        return ResponseEntity.ok(matchService.getMatchesForUser(principal.getId(), pageable));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Match createMatch(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam Long user2Id){

        return matchService.createMatch(principal.getId(), user2Id);
    }

    @GetMapping("/with/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> IsMatched(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long otherUserId){

        return ResponseEntity.ok(matchService.isMatched(principal.getId(), otherUserId));
    }

    @DeleteMapping("/{matchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId){
        matchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }

}
