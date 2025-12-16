package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.*;
import com.fiqhmaster.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    
    public UserStatsDTO getUserStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        
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
        
        // Get category-specific stats
        stats.setCategoryStats(getCategoryStats(userId));
        
        return stats;
    }
    
    public Map<String, CategoryStatsDTO> getCategoryStats(Long userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByStartedAtDesc(userId);
        Map<String, CategoryStatsDTO> categoryStatsMap = new HashMap<>();
        
        for (QuizAttempt attempt : attempts) {
            if (!attempt.getCompleted() || attempt.getCategory() == null) continue;
            
            String categoryName = attempt.getCategory().getNameAr();
            CategoryStatsDTO categoryStats = categoryStatsMap.getOrDefault(
                categoryName, 
                new CategoryStatsDTO()
            );
            
            categoryStats.setCategoryName(categoryName);
            categoryStats.setQuestionsAnswered(
                (categoryStats.getQuestionsAnswered() != null ? categoryStats.getQuestionsAnswered() : 0) 
                + attempt.getTotalQuestions()
            );
            categoryStats.setCorrectAnswers(
                (categoryStats.getCorrectAnswers() != null ? categoryStats.getCorrectAnswers() : 0) 
                + attempt.getCorrectAnswers()
            );
            
            if (categoryStats.getQuestionsAnswered() > 0) {
                categoryStats.setAccuracy(
                    (categoryStats.getCorrectAnswers().doubleValue() / categoryStats.getQuestionsAnswered()) * 100
                );
            }
            
            categoryStatsMap.put(categoryName, categoryStats);
        }
        
        return categoryStatsMap;
    }
    
    public Map<String, Object> getUserProgress(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        
        Map<String, Object> progress = new HashMap<>();
        
        // Overall progress
        progress.put("level", calculateLevel(user.getTotalQuizzes()));
        progress.put("experiencePoints", user.getTotalCorrectAnswers());
        progress.put("nextLevelAt", getNextLevelRequirement(user.getTotalQuizzes()));
        
        // Weekly progress
        progress.put("weeklyProgress", getWeeklyProgress(userId));
        
        // Monthly progress
        progress.put("monthlyProgress", getMonthlyProgress(userId));
        
        // Category progress
        progress.put("categoryProgress", getCategoryProgress(userId));
        
        return progress;
    }
    
    private int calculateLevel(int totalQuizzes) {
        if (totalQuizzes >= 100) return 5;
        if (totalQuizzes >= 50) return 4;
        if (totalQuizzes >= 20) return 3;
        if (totalQuizzes >= 10) return 2;
        return 1;
    }
    
    private int getNextLevelRequirement(int totalQuizzes) {
        if (totalQuizzes >= 100) return 200;
        if (totalQuizzes >= 50) return 100;
        if (totalQuizzes >= 20) return 50;
        if (totalQuizzes >= 10) return 20;
        return 10;
    }
    
    public Map<String, Object> getWeeklyProgress(Long userId) {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        List<QuizAttempt> weeklyAttempts = quizAttemptRepository
            .findByUserIdOrderByStartedAtDesc(userId)
            .stream()
            .filter(attempt -> attempt.getStartedAt().isAfter(weekAgo) && attempt.getCompleted())
            .collect(Collectors.toList());
        
        Map<String, Object> weeklyData = new HashMap<>();
        weeklyData.put("quizzesCompleted", weeklyAttempts.size());
        weeklyData.put("totalQuestions", weeklyAttempts.stream()
            .mapToInt(QuizAttempt::getTotalQuestions)
            .sum());
        weeklyData.put("correctAnswers", weeklyAttempts.stream()
            .mapToInt(QuizAttempt::getCorrectAnswers)
            .sum());
        
        // Daily breakdown
        Map<String, Integer> dailyBreakdown = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = LocalDateTime.now().minusDays(i);
            String dayKey = day.toLocalDate().toString();
            int count = (int) weeklyAttempts.stream()
                .filter(a -> a.getStartedAt().toLocalDate().equals(day.toLocalDate()))
                .count();
            dailyBreakdown.put(dayKey, count);
        }
        weeklyData.put("dailyBreakdown", dailyBreakdown);
        
        return weeklyData;
    }
    
    public Map<String, Object> getMonthlyProgress(Long userId) {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(1);
        List<QuizAttempt> monthlyAttempts = quizAttemptRepository
            .findByUserIdOrderByStartedAtDesc(userId)
            .stream()
            .filter(attempt -> attempt.getStartedAt().isAfter(monthAgo) && attempt.getCompleted())
            .collect(Collectors.toList());
        
        Map<String, Object> monthlyData = new HashMap<>();
        monthlyData.put("quizzesCompleted", monthlyAttempts.size());
        monthlyData.put("totalQuestions", monthlyAttempts.stream()
            .mapToInt(QuizAttempt::getTotalQuestions)
            .sum());
        monthlyData.put("correctAnswers", monthlyAttempts.stream()
            .mapToInt(QuizAttempt::getCorrectAnswers)
            .sum());
        
        // Safe average calculation with null check
        double averageScore = 0.0;
        if (!monthlyAttempts.isEmpty()) {
            averageScore = monthlyAttempts.stream()
                .filter(a -> a.getScorePercentage() != null)
                .mapToDouble(QuizAttempt::getScorePercentage)
                .average()
                .orElse(0.0);
        }
        monthlyData.put("averageScore", averageScore);
        
        return monthlyData;
    }
    
    private Map<String, Object> getCategoryProgress(Long userId) {
        Map<String, Object> categoryProgress = new HashMap<>();
        List<Category> categories = categoryRepository.findAll();
        
        for (Category category : categories) {
            List<QuizAttempt> categoryAttempts = quizAttemptRepository
                .findByUserIdOrderByStartedAtDesc(userId)
                .stream()
                .filter(a -> a.getCompleted() && 
                            a.getCategory() != null && 
                            a.getCategory().getId().equals(category.getId()))
                .collect(Collectors.toList());
            
            if (!categoryAttempts.isEmpty()) {
                Map<String, Object> progress = new HashMap<>();
                progress.put("quizzesTaken", categoryAttempts.size());
                
                // Safe average and max calculations
                double averageScore = categoryAttempts.stream()
                    .filter(a -> a.getScorePercentage() != null)
                    .mapToDouble(QuizAttempt::getScorePercentage)
                    .average()
                    .orElse(0.0);
                progress.put("averageScore", averageScore);
                
                double bestScore = categoryAttempts.stream()
                    .filter(a -> a.getScorePercentage() != null)
                    .mapToDouble(QuizAttempt::getScorePercentage)
                    .max()
                    .orElse(0.0);
                progress.put("bestScore", bestScore);
                
                categoryProgress.put(category.getNameAr(), progress);
            }
        }
        
        return categoryProgress;
    }



   
public Map<String, Object> getCategoryProgressForUser(Long userId) {
    Map<String, Object> result = new HashMap<>();
    List<Category> categories = categoryRepository.findAll();
    List<QuizAttempt> userAttempts = quizAttemptRepository
        .findByUserIdAndCompletedOrderByStartedAtDesc(userId, true);
    
    // Calculate progress for each category
    List<Map<String, Object>> categoryProgress = new ArrayList<>();
    
    for (Category category : categories) {
        // Get total questions in this category
        Long totalQuestions = questionRepository.countByCategoryId(category.getId());
        
        if (totalQuestions == 0) continue;
        
        // Get user's attempts for this category
        List<QuizAttempt> categoryAttempts = userAttempts.stream()
            .filter(a -> a.getCategory() != null && 
                        a.getCategory().getId().equals(category.getId()))
            .collect(Collectors.toList());
        
        // Calculate unique questions answered
        Set<Long> answeredQuestionIds = new HashSet<>();
        int totalCorrect = 0;
        int totalAnswered = 0;
        
        for (QuizAttempt attempt : categoryAttempts) {
            List<UserAnswer> answers = userAnswerRepository
                .findByQuizAttemptId(attempt.getId());
            
            for (UserAnswer answer : answers) {
                answeredQuestionIds.add(answer.getQuestion().getId());
                totalAnswered++;
                if (answer.getIsCorrect()) {
                    totalCorrect++;
                }
            }
        }
        
        // Calculate progress percentage
        double progress = (answeredQuestionIds.size() * 100.0) / totalQuestions;
        double accuracy = totalAnswered > 0 
            ? (totalCorrect * 100.0) / totalAnswered 
            : 0.0;
        
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("categoryId", category.getId());
        categoryData.put("categoryName", category.getNameAr());
        categoryData.put("icon", category.getIcon());
        categoryData.put("color", category.getColor());
        categoryData.put("progress", Math.min(Math.round(progress), 100));
        categoryData.put("accuracy", Math.round(accuracy));
        categoryData.put("questionsAnswered", answeredQuestionIds.size());
        categoryData.put("totalQuestions", totalQuestions.intValue());
        categoryData.put("totalCorrect", totalCorrect);
        categoryData.put("quizzesTaken", categoryAttempts.size());
        
        categoryProgress.add(categoryData);
    }
    
    // Sort by progress descending
    categoryProgress.sort((a, b) -> 
        ((Integer) b.get("progress")).compareTo((Integer) a.get("progress"))
    );
    
    result.put("categories", categoryProgress);
    result.put("totalCategories", categories.size());
    
    return result;
}
    

    public List<Map<String, Object>> getUserAchievements(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        
        List<Map<String, Object>> achievements = new ArrayList<>();
        
        // Quiz milestones
        achievements.add(createAchievement(
            "🎯", "البداية", "أكمل أول 10 اختبارات",
            user.getTotalQuizzes(), 10, user.getTotalQuizzes() >= 10
        ));
        achievements.add(createAchievement(
            "🏆", "المثابر", "أكمل 50 اختباراً",
            user.getTotalQuizzes(), 50, user.getTotalQuizzes() >= 50
        ));
        achievements.add(createAchievement(
            "👑", "الخبير", "أكمل 100 اختبار",
            user.getTotalQuizzes(), 100, user.getTotalQuizzes() >= 100
        ));
        
        // Streak achievements
        achievements.add(createAchievement(
            "🔥", "الملتزم", "سلسلة 7 أيام متتالية",
            user.getCurrentStreak(), 7, user.getCurrentStreak() >= 7
        ));
        achievements.add(createAchievement(
            "💎", "الأسطورة", "سلسلة 30 يوماً متتالية",
            user.getCurrentStreak(), 30, user.getCurrentStreak() >= 30
        ));
        
        // Accuracy achievements
        achievements.add(createAchievement(
            "⭐", "الدقيق", "100 إجابة صحيحة",
            user.getTotalCorrectAnswers(), 100, user.getTotalCorrectAnswers() >= 100
        ));
        achievements.add(createAchievement(
            "💫", "المتفوق", "500 إجابة صحيحة",
            user.getTotalCorrectAnswers(), 500, user.getTotalCorrectAnswers() >= 500
        ));
        achievements.add(createAchievement(
            "✨", "العبقري", "1000 إجابة صحيحة",
            user.getTotalCorrectAnswers(), 1000, user.getTotalCorrectAnswers() >= 1000
        ));
        
        // Accuracy percentage achievement
        if (user.getTotalAnswers() >= 50) {
            double accuracy = (double) user.getTotalCorrectAnswers() / user.getTotalAnswers();
            achievements.add(createAchievement(
                "🎓", "المتميز", "دقة 90% أو أكثر (50+ سؤال)",
                (int)(accuracy * 100), 90, accuracy >= 0.9
            ));
        }
        
        return achievements;
    }
    
    private Map<String, Object> createAchievement(
            String icon, String title, String description, 
            int current, int target, boolean unlocked) {
        Map<String, Object> achievement = new HashMap<>();
        achievement.put("icon", icon);
        achievement.put("title", title);
        achievement.put("description", description);
        achievement.put("current", current);
        achievement.put("target", target);
        achievement.put("unlocked", unlocked);
        achievement.put("progress", Math.min((double) current / target * 100, 100.0));
        return achievement;
    }
    
    public Map<String, Object> getPerformanceTrends(Long userId, String period) {
        LocalDateTime startDate;
        switch (period) {
            case "7d":
                startDate = LocalDateTime.now().minusDays(7);
                break;
            case "30d":
                startDate = LocalDateTime.now().minusDays(30);
                break;
            case "90d":
                startDate = LocalDateTime.now().minusDays(90);
                break;
            default:
                startDate = LocalDateTime.now().minusDays(7);
        }
        
        List<QuizAttempt> attempts = quizAttemptRepository
            .findByUserIdOrderByStartedAtDesc(userId)
            .stream()
            .filter(a -> a.getCompleted() && a.getStartedAt().isAfter(startDate))
            .sorted(Comparator.comparing(QuizAttempt::getStartedAt))
            .collect(Collectors.toList());
        
        Map<String, Object> trends = new HashMap<>();
        
        List<Map<String, Object>> scoreHistory = new ArrayList<>();
        for (QuizAttempt attempt : attempts) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("date", attempt.getStartedAt());
            entry.put("score", attempt.getScorePercentage() != null ? attempt.getScorePercentage() : 0.0);
            scoreHistory.add(entry);
        }
        
        trends.put("scoreHistory", scoreHistory);
        
        double averageScore = attempts.stream()
            .filter(a -> a.getScorePercentage() != null)
            .mapToDouble(QuizAttempt::getScorePercentage)
            .average()
            .orElse(0.0);
        trends.put("averageScore", averageScore);
        trends.put("improvement", calculateImprovement(attempts));
        
        return trends;
    }
    
    private double calculateImprovement(List<QuizAttempt> attempts) {
        if (attempts.size() < 2) return 0.0;
        
        int halfSize = attempts.size() / 2;
        
        double firstHalfAvg = attempts.subList(0, halfSize).stream()
            .filter(a -> a.getScorePercentage() != null)
            .mapToDouble(QuizAttempt::getScorePercentage)
            .average()
            .orElse(0.0);
            
        double secondHalfAvg = attempts.subList(halfSize, attempts.size()).stream()
            .filter(a -> a.getScorePercentage() != null)
            .mapToDouble(QuizAttempt::getScorePercentage)
            .average()
            .orElse(0.0);
        
        return secondHalfAvg - firstHalfAvg;
    }

    
}