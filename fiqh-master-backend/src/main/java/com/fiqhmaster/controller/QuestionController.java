package com.fiqhmaster.controller;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    
    private final QuestionService questionService;
    
    @GetMapping("/random")
    public ResponseEntity<List<QuestionDTO>> getRandomQuestions(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(questionService.getRandomQuestions(limit, userId));
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(questionService.getQuestionsByCategory(categoryId, limit, userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<QuestionAnswerDTO> getQuestionById(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(questionService.getQuestionWithAnswer(id, userId));
    }
}