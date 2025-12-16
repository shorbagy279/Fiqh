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
    
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO updatedUser = userService.updateUserProfile(userId, updates);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<UserStatsDTO> getStats(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(statsService.getUserStats(userId));
    }
    
    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(statsService.getUserProgress(userId));
    }
    
    @GetMapping("/achievements")
    public ResponseEntity<List<Map<String, Object>>> getAchievements(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(statsService.getUserAchievements(userId));
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
        
        return ResponseEntity.ok(Map.of("message", "تمت إضافة الإشارة المرجعية بنجاح"));
    }
    
    @DeleteMapping("/bookmarks/{questionId}")
    public ResponseEntity<Map<String, String>> removeBookmark(
            @PathVariable Long questionId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        bookmarkService.removeBookmark(userId, questionId);
        
        return ResponseEntity.ok(Map.of("message", "تمت إزالة الإشارة المرجعية بنجاح"));
    }
    
    @PutMapping("/bookmarks/{questionId}/notes")
    public ResponseEntity<Map<String, String>> updateBookmarkNotes(
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String notes = body.get("notes");
        bookmarkService.updateBookmarkNotes(userId, questionId, notes);
        
        return ResponseEntity.ok(Map.of("message", "تم تحديث الملاحظات بنجاح"));
    }
    
    // Settings endpoints
    @PutMapping("/settings/difficulty")
    public ResponseEntity<Map<String, String>> updateDifficulty(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String level = body.get("level");
        userService.updateDifficultyLevel(userId, level);
        
        return ResponseEntity.ok(Map.of("message", "تم تحديث مستوى الصعوبة بنجاح"));
    }
    
    @PutMapping("/settings/reminders")
    public ResponseEntity<Map<String, String>> updateReminders(
            @RequestBody Map<String, Boolean> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Boolean enabled = body.get("enabled");
        userService.updateDailyReminders(userId, enabled);
        
        return ResponseEntity.ok(Map.of("message", "تم تحديث التذكيرات بنجاح"));
    }
    
    @PutMapping("/settings/language")
    public ResponseEntity<Map<String, String>> updateLanguage(
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String language = body.get("language");
        userService.updatePreferredLanguage(userId, language);
        
        return ResponseEntity.ok(Map.of("message", "تم تحديث اللغة بنجاح"));
    }
    
    @PutMapping("/settings/marja")
    public ResponseEntity<Map<String, String>> updateMarja(
            @RequestBody Map<String, Long> body,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Long marjaId = body.get("marjaId");
        userService.updatePreferredMarja(userId, marjaId);
        
        return ResponseEntity.ok(Map.of("message", "تم تحديث المرجع الديني بنجاح"));
    }
    
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportUserData(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(userService.exportUserData(userId));
    }
    
    @PostMapping("/import")
    public ResponseEntity<Map<String, String>> importUserData(
            @RequestBody Map<String, Object> data,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        userService.importUserData(userId, data);
        
        return ResponseEntity.ok(Map.of("message", "تم استيراد البيانات بنجاح"));
    }
}