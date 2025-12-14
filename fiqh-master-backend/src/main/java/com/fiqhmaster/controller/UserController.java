package com.fiqhmaster.controller;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.service.UserService;
import com.fiqhmaster.service.BookmarkService;
import com.fiqhmaster.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final BookmarkService bookmarkService;
    private final StatsService statsService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(userService.toDTO(userService.findById(userId)));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getStats(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(statsService.getUserStats(userId));
    }
    
    @GetMapping("/bookmarks")
    public ResponseEntity<List<QuestionDTO>> getBookmarks(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId));
    }
    
    @PostMapping("/bookmarks/{questionId}")
    public ResponseEntity<Map<String, String>> addBookmark(
            @PathVariable Long questionId,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String notes = body != null ? body.get("notes") : null;
        bookmarkService.addBookmark(userId, questionId, notes);
        
        return ResponseEntity.ok(Map.of("message", "Bookmark added successfully"));
    }
    
    @DeleteMapping("/bookmarks/{questionId}")
    public ResponseEntity<Map<String, String>> removeBookmark(
            @PathVariable Long questionId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.removeBookmark(userId, questionId);
        
        return ResponseEntity.ok(Map.of("message", "Bookmark removed successfully"));
    }
}