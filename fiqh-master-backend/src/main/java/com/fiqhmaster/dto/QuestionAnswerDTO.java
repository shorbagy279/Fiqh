package com.fiqhmaster.dto;

import lombok.Data;

@Data
public class QuestionAnswerDTO extends QuestionDTO {
    private Integer correctAnswer;
    private String explanationAr;
    private String explanationEn;
    private String referenceAr;
    private String referenceEn;
}