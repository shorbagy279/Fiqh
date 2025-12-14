package com.fiqhmaster.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(name = "preferred_language", length = 5)
    private String preferredLanguage = "ar";
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preferred_marja_id")
    private Marja preferredMarja;
    
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel = "intermediate";
    
    @Column(name = "daily_reminders")
    private Boolean dailyReminders = false;
    
    @Column(name = "current_streak")
    private Integer currentStreak = 0;
    
    @Column(name = "longest_streak")
    private Integer longestStreak = 0;
    
    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;
    
    @Column(name = "total_quizzes")
    private Integer totalQuizzes = 0;
    
    @Column(name = "total_correct_answers")
    private Integer totalCorrectAnswers = 0;
    
    @Column(name = "total_answers")
    private Integer totalAnswers = 0;
    
    @Column(name = "current_rank", length = 50)
    private String currentRank = "فقيه مبتدئ";
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "badge")
    private Set<String> badges = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}