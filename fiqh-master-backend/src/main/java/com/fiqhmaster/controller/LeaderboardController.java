package com.fiqhmaster.controller;

import com.fiqhmaster.dto.LeaderboardEntryDTO;
import com.fiqhmaster.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    
    private final LeaderboardService leaderboardService;
    
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDTO>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(limit));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<LeaderboardEntryDTO>> getCategoryLeaderboard(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(leaderboardService.getCategoryLeaderboard(categoryId, limit));
    }
    
    @GetMapping("/streak")
    public ResponseEntity<List<LeaderboardEntryDTO>> getStreakLeaderboard(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(leaderboardService.getStreakLeaderboard(limit));
    }
}
