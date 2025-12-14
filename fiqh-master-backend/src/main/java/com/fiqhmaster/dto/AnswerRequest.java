package com.fiqhmaster.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AnswerRequest {
    @NotNull(message = "Question ID is required")
    private Long questionId;
    
    @NotNull(message = "Quiz attempt ID is required")
    private Long quizAttemptId;
    
    @NotNull(message = "Selected answer is required")
    @Min(value = 0, message = "Answer must be between 0 and 3")
    @Max(value = 3, message = "Answer must be between 0 and 3")
    private Integer selectedAnswer;
    
    private Integer timeTakenSeconds;
}