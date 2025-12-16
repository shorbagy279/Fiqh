package com.fiqhmaster.repository;

import com.fiqhmaster.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Find by category
    List<Question> findByCategoryId(Long categoryId);
    
    // Find by difficulty
    List<Question> findByDifficulty(String difficulty);
    
    // Find by category and difficulty
    List<Question> findByCategoryIdAndDifficulty(Long categoryId, String difficulty);
    
    // Find by multiple categories
    @Query("SELECT q FROM Question q WHERE q.category.id IN :categoryIds")
    List<Question> findByCategoryIdIn(@Param("categoryIds") List<Long> categoryIds);
    
    // Find by multiple categories and difficulty
    @Query("SELECT q FROM Question q WHERE q.category.id IN :categoryIds AND q.difficulty = :difficulty")
    List<Question> findByCategoryIdInAndDifficulty(
        @Param("categoryIds") List<Long> categoryIds, 
        @Param("difficulty") String difficulty
    );
    
    // Count by category
    Long countByCategoryId(Long categoryId);
    
    // Count by difficulty
    Long countByDifficulty(String difficulty);
    
    // Count by category and difficulty
    Long countByCategoryIdAndDifficulty(Long categoryId, String difficulty);
    
    // Random questions
    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
    
    // Random questions by category
    @Query(value = "SELECT * FROM questions WHERE category_id = :categoryId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(
        @Param("categoryId") Long categoryId, 
        @Param("limit") int limit
    );
    
    // Random questions by difficulty
    @Query(value = "SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByDifficulty(
        @Param("difficulty") String difficulty, 
        @Param("limit") int limit
    );
}