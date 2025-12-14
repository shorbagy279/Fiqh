package com.fiqhmaster.dto;

import lombok.Data;

@Data
public class QuizStartRequest {
    private Long categoryId;
    private String quizType = "random";
    private Integer questionCount = 10;
}