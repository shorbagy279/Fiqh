package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.User;
import com.fiqhmaster.entity.Marja;
import com.fiqhmaster.repository.UserRepository;
import com.fiqhmaster.repository.MarjaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final MarjaRepository marjaRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("البريد الإلكتروني مسجل بالفعل");
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
        
        log.info("User registered successfully: {}", user.getEmail());
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
    }
    
    @Transactional
    public UserDTO updateUserProfile(Long userId, Map<String, Object> updates) {
        User user = findById(userId);
        
        if (updates.containsKey("fullName")) {
            user.setFullName((String) updates.get("fullName"));
        }
        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("البريد الإلكتروني مستخدم بالفعل");
            }
            user.setEmail(newEmail);
        }
        
        userRepository.save(user);
        log.info("User profile updated: {}", user.getEmail());
        return toDTO(user);
    }
    
    @Transactional
    public void updateStreak(Long userId) {
        User user = findById(userId);
        LocalDate today = LocalDate.now();
        LocalDate lastActivityDate = user.getLastActivityDate() != null 
            ? user.getLastActivityDate().toLocalDate() 
            : null;
        
        log.debug("Updating streak for user {}: today={}, lastActivity={}", userId, today, lastActivityDate);
        
        if (lastActivityDate == null) {
            // First time activity
            user.setCurrentStreak(1);
            user.setLongestStreak(1);
            log.info("First activity for user {}, streak set to 1", userId);
        } else if (lastActivityDate.equals(today)) {
            // Already active today, don't change streak
            log.debug("User {} already active today, no streak change", userId);
            user.setLastActivityDate(LocalDateTime.now());
            userRepository.save(user);
            return;
        } else if (lastActivityDate.equals(today.minusDays(1))) {
            // Consecutive day
            user.setCurrentStreak(user.getCurrentStreak() + 1);
            log.info("Consecutive day for user {}, streak increased to {}", userId, user.getCurrentStreak());
            
            // Award streak badges
            if (user.getCurrentStreak() == 7 && !user.getBadges().contains("🔥")) {
                user.getBadges().add("🔥");
                log.info("User {} earned 7-day streak badge", userId);
            }
            if (user.getCurrentStreak() == 30 && !user.getBadges().contains("💎")) {
                user.getBadges().add("💎");
                log.info("User {} earned 30-day streak badge", userId);
            }
            if (user.getCurrentStreak() == 100 && !user.getBadges().contains("🏅")) {
                user.getBadges().add("🏅");
                log.info("User {} earned 100-day streak badge", userId);
            }
        } else {
            // Streak broken
            log.info("Streak broken for user {}, resetting from {} to 1", userId, user.getCurrentStreak());
            user.setCurrentStreak(1);
        }
        
        // Update longest streak if current is higher
        if (user.getCurrentStreak() > user.getLongestStreak()) {
            user.setLongestStreak(user.getCurrentStreak());
            log.info("New longest streak for user {}: {}", userId, user.getLongestStreak());
        }
        
        user.setLastActivityDate(LocalDateTime.now());
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
        log.info("User stats updated: userId={}, quizzes={}, correct={}/{}", 
            userId, user.getTotalQuizzes(), user.getTotalCorrectAnswers(), user.getTotalAnswers());
    }
    
    private void updateRank(User user) {
        int totalQuizzes = user.getTotalQuizzes();
        int correctAnswers = user.getTotalCorrectAnswers();
        
        String oldRank = user.getCurrentRank();
        
        if (totalQuizzes >= 100 && correctAnswers >= 800) {
            user.setCurrentRank("فقيه خبير");
        } else if (totalQuizzes >= 50 && correctAnswers >= 350) {
            user.setCurrentRank("فقيه متقدم");
        } else if (totalQuizzes >= 20 && correctAnswers >= 120) {
            user.setCurrentRank("فقيه متوسط");
        } else if (totalQuizzes >= 10) {
            user.setCurrentRank("فقيه ناشئ");
        } else {
            user.setCurrentRank("فقيه مبتدئ");
        }
        
        if (!oldRank.equals(user.getCurrentRank())) {
            log.info("User {} rank updated from '{}' to '{}'", user.getId(), oldRank, user.getCurrentRank());
        }
    }
    
    private void updateBadges(User user) {
        Set<String> badges = user.getBadges();
        
        // Quiz completion badges
        if (user.getTotalQuizzes() >= 10 && !badges.contains("🎯")) {
            badges.add("🎯");
            log.info("User {} earned 10 quizzes badge", user.getId());
        }
        if (user.getTotalQuizzes() >= 50 && !badges.contains("🏆")) {
            badges.add("🏆");
            log.info("User {} earned 50 quizzes badge", user.getId());
        }
        if (user.getTotalQuizzes() >= 100 && !badges.contains("👑")) {
            badges.add("👑");
            log.info("User {} earned 100 quizzes badge", user.getId());
        }
        
        // Correct answers badges
        if (user.getTotalCorrectAnswers() >= 100 && !badges.contains("⭐")) {
            badges.add("⭐");
            log.info("User {} earned 100 correct answers badge", user.getId());
        }
        if (user.getTotalCorrectAnswers() >= 500 && !badges.contains("💫")) {
            badges.add("💫");
            log.info("User {} earned 500 correct answers badge", user.getId());
        }
        if (user.getTotalCorrectAnswers() >= 1000 && !badges.contains("✨")) {
            badges.add("✨");
            log.info("User {} earned 1000 correct answers badge", user.getId());
        }
        
        // Accuracy badges
        if (user.getTotalAnswers() >= 50) {
            double accuracy = (double) user.getTotalCorrectAnswers() / user.getTotalAnswers();
            if (accuracy >= 0.9 && !badges.contains("🎓")) {
                badges.add("🎓");
                log.info("User {} earned 90% accuracy badge", user.getId());
            }
        }
    }
    
    @Transactional
    public void updateDifficultyLevel(Long userId, String level) {
        User user = findById(userId);
        user.setDifficultyLevel(level);
        userRepository.save(user);
        log.info("User {} difficulty level updated to {}", userId, level);
    }
    
    @Transactional
    public void updateDailyReminders(Long userId, Boolean enabled) {
        User user = findById(userId);
        user.setDailyReminders(enabled);
        userRepository.save(user);
        log.info("User {} daily reminders set to {}", userId, enabled);
    }
    
    @Transactional
    public void updatePreferredLanguage(Long userId, String language) {
        User user = findById(userId);
        user.setPreferredLanguage(language);
        userRepository.save(user);
        log.info("User {} language updated to {}", userId, language);
    }
    
    @Transactional
    public void updatePreferredMarja(Long userId, Long marjaId) {
        User user = findById(userId);
        if (marjaId != null) {
            Marja marja = marjaRepository.findById(marjaId)
                .orElseThrow(() -> new RuntimeException("المرجع غير موجود"));
            user.setPreferredMarja(marja);
            log.info("User {} marja updated to {}", userId, marja.getNameAr());
        } else {
            user.setPreferredMarja(null);
            log.info("User {} marja cleared", userId);
        }
        userRepository.save(user);
    }
    
    public Map<String, Object> exportUserData(Long userId) {
        User user = findById(userId);
        Map<String, Object> data = new HashMap<>();
        
        data.put("profile", toDTO(user));
        data.put("stats", Map.of(
            "totalQuizzes", user.getTotalQuizzes(),
            "totalCorrectAnswers", user.getTotalCorrectAnswers(),
            "totalAnswers", user.getTotalAnswers(),
            "currentStreak", user.getCurrentStreak(),
            "longestStreak", user.getLongestStreak()
        ));
        data.put("badges", user.getBadges());
        data.put("exportDate", LocalDateTime.now());
        
        log.info("User data exported for user {}", userId);
        return data;
    }
    
    @Transactional
    public void importUserData(Long userId, Map<String, Object> data) {
        User user = findById(userId);
        
        // Import stats if provided
        if (data.containsKey("stats")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) data.get("stats");
            
            if (stats.containsKey("totalQuizzes")) {
                user.setTotalQuizzes((Integer) stats.get("totalQuizzes"));
            }
            if (stats.containsKey("totalCorrectAnswers")) {
                user.setTotalCorrectAnswers((Integer) stats.get("totalCorrectAnswers"));
            }
            if (stats.containsKey("totalAnswers")) {
                user.setTotalAnswers((Integer) stats.get("totalAnswers"));
            }
        }
        
        // Import badges if provided
        if (data.containsKey("badges")) {
            @SuppressWarnings("unchecked")
            List<String> badges = (List<String>) data.get("badges");
            user.getBadges().addAll(badges);
        }
        
        userRepository.save(user);
        log.info("User data imported for user {}", userId);
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