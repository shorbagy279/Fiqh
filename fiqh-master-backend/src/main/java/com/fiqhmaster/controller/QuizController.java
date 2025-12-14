package com.fiqhmaster.controller;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.QuizAttempt;
import com.fiqhmaster.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {
    
    private final QuizService quizService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startQuiz(
            @RequestBody QuizStartRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        QuizAttempt attempt = quizService.startQuiz(userId, request);
        
        return ResponseEntity.ok(Map.of(
            "quizAttemptId", attempt.getId(),
            "totalQuestions", attempt.getTotalQuestions(),
            "startedAt", attempt.getStartedAt()
        ));
    }
    
    @PostMapping("/answer")
    public ResponseEntity<Map<String, String>> submitAnswer(
            @Valid @RequestBody AnswerRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        quizService.submitAnswer(userId, request);
        
        return ResponseEntity.ok(Map.of("message", "Answer submitted successfully"));
    }
    
    @PostMapping("/complete/{quizAttemptId}")
    public ResponseEntity<QuizResultDTO> completeQuiz(
            @PathVariable Long quizAttemptId,
            @RequestParam(defaultValue = "0") Integer timeTaken,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        QuizResultDTO result = quizService.completeQuiz(userId, quizAttemptId, timeTaken);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<QuizResultDTO>> getQuizHistory(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(quizService.getUserQuizHistory(userId));
    }
    
    @GetMapping("/{quizAttemptId}")
    public ResponseEntity<QuizResultDTO> getQuizResult(
            @PathVariable Long quizAttemptId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(quizService.getQuizResult(userId, quizAttemptId));
    }
}