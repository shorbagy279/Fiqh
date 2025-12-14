package com.fiqhmaster.repository;

import com.fiqhmaster.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u ORDER BY u.totalCorrectAnswers DESC, u.totalQuizzes DESC")
    List<User> findTopUsers();
    
    @Query("SELECT u FROM User u WHERE u.currentStreak > 0 ORDER BY u.currentStreak DESC")
    List<User> findUsersByStreak();
}