package com.fiqhmaster.dto;

import lombok.Data;
import java.util.Map;
import java.util.Set;

@Data
public class UserStatsDTO {
    private Integer totalQuizzes;
    private Integer totalCorrectAnswers;
    private Integer totalAnswers;
    private Double overallAccuracy;
    private Integer currentStreak;
    private Integer longestStreak;
    private String currentRank;
    private Set<String> badges;
    private Map<String, CategoryStatsDTO> categoryStats;
}