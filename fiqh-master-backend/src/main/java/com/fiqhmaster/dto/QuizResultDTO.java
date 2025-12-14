package com.fiqhmaster.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizResultDTO {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private String quizType;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double scorePercentage;
    private Integer timeTakenSeconds;
    private LocalDateTime completedAt;
}