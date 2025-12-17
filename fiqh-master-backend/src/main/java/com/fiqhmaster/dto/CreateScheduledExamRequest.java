package com.fiqhmaster.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateScheduledExamRequest {
    private String title;
    private String description;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private List<Long> questionIds;
    private List<Long> categoryIds;
    private Integer maxParticipants;
}




