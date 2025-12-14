package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.User;
import com.fiqhmaster.entity.Marja;
import com.fiqhmaster.repository.UserRepository;
import com.fiqhmaster.repository.MarjaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MarjaRepository marjaRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPreferredLanguage(request.getPreferredLanguage());
        
        if (request.getPreferredMarjaId() != null) {
            Marja marja = marjaRepository.findById(request.getPreferredMarjaId())
                .orElse(null);
            user.setPreferredMarja(marja);
        }
        
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    @Transactional
    public void updateStreak(Long userId) {
        User user = findById(userId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastActivity = user.getLastActivityDate();
        
        if (lastActivity == null || lastActivity.toLocalDate().isBefore(now.toLocalDate().minusDays(1))) {
            if (lastActivity != null && lastActivity.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
                user.setCurrentStreak(user.getCurrentStreak() + 1);
            } else {
                user.setCurrentStreak(1);
            }
        }
        
        if (user.getCurrentStreak() > user.getLongestStreak()) {
            user.setLongestStreak(user.getCurrentStreak());
        }
        
        user.setLastActivityDate(now);
        userRepository.save(user);
    }
    
    @Transactional
    public void updateUserStats(Long userId, int correctAnswers, int totalAnswers) {
        User user = findById(userId);
        user.setTotalQuizzes(user.getTotalQuizzes() + 1);
        user.setTotalCorrectAnswers(user.getTotalCorrectAnswers() + correctAnswers);
        user.setTotalAnswers(user.getTotalAnswers() + totalAnswers);
        
        updateRank(user);
        updateBadges(user);
        
        userRepository.save(user);
    }
    
    private void updateRank(User user) {
        int totalQuizzes = user.getTotalQuizzes();
        if (totalQuizzes >= 100) {
            user.setCurrentRank("فقيه خبير");
        } else if (totalQuizzes >= 50) {
            user.setCurrentRank("فقيه متقدم");
        } else if (totalQuizzes >= 20) {
            user.setCurrentRank("فقيه متوسط");
        } else {
            user.setCurrentRank("فقيه مبتدئ");
        }
    }
    
    private void updateBadges(User user) {
        if (user.getTotalQuizzes() >= 10 && !user.getBadges().contains("🎯")) {
            user.getBadges().add("🎯");
        }
        if (user.getCurrentStreak() >= 7 && !user.getBadges().contains("🔥")) {
            user.getBadges().add("🔥");
        }
        if (user.getTotalCorrectAnswers() >= 100 && !user.getBadges().contains("⭐")) {
            user.getBadges().add("⭐");
        }
    }
    
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setDifficultyLevel(user.getDifficultyLevel());
        dto.setDailyReminders(user.getDailyReminders());
        dto.setCurrentStreak(user.getCurrentStreak());
        dto.setLongestStreak(user.getLongestStreak());
        dto.setTotalQuizzes(user.getTotalQuizzes());
        dto.setTotalCorrectAnswers(user.getTotalCorrectAnswers());
        dto.setTotalAnswers(user.getTotalAnswers());
        dto.setCurrentRank(user.getCurrentRank());
        dto.setBadges(user.getBadges());
        dto.setCreatedAt(user.getCreatedAt());
        
        if (user.getPreferredMarja() != null) {
            dto.setPreferredMarjaId(user.getPreferredMarja().getId());
            dto.setPreferredMarjaName(user.getPreferredMarja().getNameAr());
        }
        
        return dto;
    }
}

