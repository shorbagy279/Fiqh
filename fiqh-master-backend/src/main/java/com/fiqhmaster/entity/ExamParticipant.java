package com.fiqhmaster.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_participants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private ScheduledExam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "joined_at")
    @CreationTimestamp
    private LocalDateTime joinedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "quiz_attempt_id")
    private Long quizAttemptId;
    
    @Column(name = "status")
    private String status = "REGISTERED"; // REGISTERED, STARTED, COMPLETED
    
    @Column(name = "score")
    private Integer score;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}