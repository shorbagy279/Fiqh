package com.fiqhmaster.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "marjas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Marja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name_ar", nullable = false, length = 100)
    private String nameAr;
    
    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;
    
    @Column(name = "description_ar", columnDefinition = "TEXT")
    private String descriptionAr;
    
    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
