package com.fiqhmaster.repository;

import com.fiqhmaster.entity.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByQuizAttemptId(Long quizAttemptId);
    
    @Query("SELECT ua FROM UserAnswer ua WHERE ua.quizAttempt.user.id = :userId AND ua.isCorrect = false ORDER BY ua.answeredAt DESC")
    List<UserAnswer> findIncorrectAnswersByUser(@Param("userId") Long userId);
}