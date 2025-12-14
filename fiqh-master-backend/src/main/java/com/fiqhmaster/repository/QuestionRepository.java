package com.fiqhmaster.repository;

import com.fiqhmaster.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    List<Question> findByCategoryId(Long categoryId);
    
    List<Question> findByDifficulty(String difficulty);
    
    @Query(value = "SELECT * FROM questions WHERE is_active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM questions WHERE category_id = :categoryId AND is_active = true ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);
}
