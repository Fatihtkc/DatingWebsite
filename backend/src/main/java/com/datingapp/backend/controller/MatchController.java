package com.datingapp.backend.controller;

import com.datingapp.backend.model.Match;
import com.datingapp.backend.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@CrossOrigin(origins = "http://localhost:3000")
public class MatchController {

    private final MatchService matchService;
    public MatchController(MatchService matchService) { this.matchService = matchService; }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<List<Match>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(matchService.getMatchesForUser(userId));
    }

    @PostMapping
    public Match createMatch(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        return matchService.createMatch(user1Id, user2Id);
    }

    @GetMapping("/{u1}/{u2}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> exists(@PathVariable Long u1, @PathVariable Long u2) {
        return ResponseEntity.ok(matchService.isMatched(u1, u2));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
    matchService.deleteMatch(id);
    return ResponseEntity.noContent().build();
}

}
