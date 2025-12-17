package com.fiqhmaster.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scheduled_exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledExam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;
    
    @Column(name = "exam_code", unique = true, nullable = false, length = 8)
    private String examCode;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;
    
    @Column(name = "question_ids", columnDefinition = "TEXT")
    private String questionIds; // Comma-separated question IDs
    
    @Column(name = "category_ids", columnDefinition = "TEXT")
    private String categoryIds; // Comma-separated category IDs
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "max_participants")
    private Integer maxParticipants;
    
    @Column(name = "current_participants")
    private Integer currentParticipants = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    public void generateCode() {
        if (this.examCode == null) {
            this.examCode = generateUniqueCode();
        }
    }
    
    private String generateUniqueCode() {
        return UUID.randomUUID().toString()
            .replace("-", "")
            .substring(0, 8)
            .toUpperCase();
    }
    
    public boolean isStarted() {
        return LocalDateTime.now().isAfter(startTime);
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(startTime.plusMinutes(durationMinutes));
    }
    
    public boolean canJoin() {
        return isActive && 
               !isExpired() && 
               (maxParticipants == null || currentParticipants < maxParticipants);
    }
}