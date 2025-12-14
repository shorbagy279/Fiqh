package com.fiqhmaster.dto;

import lombok.Data;

@Data
public class CategoryStatsDTO {
    private String categoryName;
    private Integer questionsAnswered;
    private Integer correctAnswers;
    private Double accuracy;
}
