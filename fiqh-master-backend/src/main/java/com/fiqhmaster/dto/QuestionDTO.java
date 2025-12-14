package com.fiqhmaster.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String questionAr;
    private String questionEn;
    private List<String> optionsAr;
    private List<String> optionsEn;
    private String difficulty;
    private Boolean isBookmarked;
}