package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.User;
import com.fiqhmaster.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    
    public UserStatsDTO getUserStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserStatsDTO stats = new UserStatsDTO();
        stats.setTotalQuizzes(user.getTotalQuizzes());
        stats.setTotalCorrectAnswers(user.getTotalCorrectAnswers());
        stats.setTotalAnswers(user.getTotalAnswers());
        
        double accuracy = user.getTotalAnswers() > 0 
            ? (user.getTotalCorrectAnswers().doubleValue() / user.getTotalAnswers()) * 100 
            : 0.0;
        stats.setOverallAccuracy(accuracy);
        
        stats.setCurrentStreak(user.getCurrentStreak());
        stats.setLongestStreak(user.getLongestStreak());
        stats.setCurrentRank(user.getCurrentRank());
        stats.setBadges(user.getBadges());
        
        stats.setCategoryStats(new HashMap<>());
        
        return stats;
    }
}