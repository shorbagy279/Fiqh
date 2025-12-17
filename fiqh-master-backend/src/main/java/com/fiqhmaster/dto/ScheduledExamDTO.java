package com.fiqhmaster.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledExamDTO {
    private Long id;
    private String examCode;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Boolean isActive;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean isStarted;
    private Boolean isExpired;
    private Boolean canJoin;
    private Boolean isRegistered;
    private String creatorName;
    private LocalDateTime createdAt;
}
