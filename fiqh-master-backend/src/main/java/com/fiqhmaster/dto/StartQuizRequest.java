package com.fiqhmaster.dto;

import lombok.Data;
import java.util.List;

@Data
public class StartQuizRequest {
    private String quizType; // 'random', 'category', 'custom'
    private Integer questionCount = 10; // Default to 10
    private Long categoryId; // For 'category' type
    private List<Long> categoryIds; // For 'custom' type - multiple categories
    private String difficulty; // 'beginner', 'intermediate', 'advanced', 'all'
}