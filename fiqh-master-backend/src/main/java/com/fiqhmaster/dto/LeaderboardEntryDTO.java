package com.fiqhmaster.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private Long userId;
    private String fullName;
    private Integer totalQuizzes;
    private Integer totalCorrectAnswers;
    private Integer currentStreak;
    private String currentRank;
    private Integer rank;
}