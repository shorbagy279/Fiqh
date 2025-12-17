package com.fiqhmaster.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamParticipantDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String status;
    private Integer score;
    private LocalDateTime joinedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}