package com.fiqhmaster.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marja_id")
    private Marja marja;
    
    @Column(name = "question_ar", nullable = false, columnDefinition = "TEXT")
    private String questionAr;
    
    @Column(name = "question_en", columnDefinition = "TEXT")
    private String questionEn;
    
    @Column(name = "option_a_ar", nullable = false, length = 500)
    private String optionAAr;
    
    @Column(name = "option_b_ar", nullable = false, length = 500)
    private String optionBAr;
    
    @Column(name = "option_c_ar", nullable = false, length = 500)
    private String optionCAr;
    
    @Column(name = "option_d_ar", nullable = false, length = 500)
    private String optionDAr;
    
    @Column(name = "option_a_en", length = 500)
    private String optionAEn;
    
    @Column(name = "option_b_en", length = 500)
    private String optionBEn;
    
    @Column(name = "option_c_en", length = 500)
    private String optionCEn;
    
    @Column(name = "option_d_en", length = 500)
    private String optionDEn;
    
    @Column(name = "correct_answer", nullable = false)
    private Integer correctAnswer;
    
    @Column(name = "explanation_ar", nullable = false, columnDefinition = "TEXT")
    private String explanationAr;
    
    @Column(name = "explanation_en", columnDefinition = "TEXT")
    private String explanationEn;
    
    @Column(name = "reference_ar", length = 500)
    private String referenceAr;
    
    @Column(name = "reference_en", length = 500)
    private String referenceEn;
    
    @Column(length = 50)
    private String difficulty = "intermediate";
    
    @Column(length = 500)
    private String tags;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "times_answered")
    private Integer timesAnswered = 0;
    
    @Column(name = "times_correct")
    private Integer timesCorrect = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
