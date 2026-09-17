package com.datingapp.backend.controller;

import com.datingapp.backend.dto.Match.MatchDTO;
import com.datingapp.backend.security.CustomUserDetails;
import com.datingapp.backend.service.MatchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<List<MatchDTO>> getMyMatches(@AuthenticationPrincipal CustomUserDetails principal) {
        Long userId = principal.getId();
        return ResponseEntity.ok(matchService.getMatchesForUser(userId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public MatchDTO createMatch(@AuthenticationPrincipal CustomUserDetails principal, @RequestParam Long user2Id) {
        Long user1Id = principal.getId();
        return matchService.createMatch(user1Id, user2Id);
    }

    @GetMapping("/with/{otherUserId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> IsMatched(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long otherUserId) {
        Long userId = principal.getId();
        return ResponseEntity.ok(matchService.isMatched(userId, otherUserId));
    }

    @DeleteMapping("/{matchId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.noContent().build();
    }

}
