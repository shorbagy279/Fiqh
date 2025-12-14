package com.fiqhmaster.repository;

import com.fiqhmaster.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdOrderByStartedAtDesc(Long userId);
    
    List<QuizAttempt> findByUserIdAndCompletedOrderByStartedAtDesc(Long userId, Boolean completed);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.category.id = :categoryId AND qa.completed = true ORDER BY qa.startedAt DESC")
    List<QuizAttempt> findByUserAndCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.completed = true")
    Long countCompletedQuizzesByUser(@Param("userId") Long userId);
}