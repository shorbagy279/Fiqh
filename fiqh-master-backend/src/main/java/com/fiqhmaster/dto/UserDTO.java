package com.fiqhmaster.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String preferredLanguage;
    private Long preferredMarjaId;
    private String preferredMarjaName;
    private String difficultyLevel;
    private Boolean dailyReminders;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalQuizzes;
    private Integer totalCorrectAnswers;
    private Integer totalAnswers;
    private String currentRank;
    private Set<String> badges;
    private LocalDateTime createdAt;
}