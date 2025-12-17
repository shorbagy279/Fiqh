package com.fiqhmaster.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDetailsDTO {
    private ScheduledExamDTO exam;
    private List<ExamParticipantDTO> participants;
    private Boolean userRegistered;
    private String userStatus;
}
