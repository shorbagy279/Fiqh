package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.*;
import com.fiqhmaster.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final QuestionService questionService;
    
    @Transactional
    public QuizAttempt startQuiz(Long userId, QuizStartRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuizType(request.getQuizType());
        attempt.setTotalQuestions(request.getQuestionCount());
        attempt.setCorrectAnswers(0);
        attempt.setCompleted(false);
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            attempt.setCategory(category);
        }
        
        return quizAttemptRepository.save(attempt);
    }
    
    @Transactional
    public void submitAnswer(Long userId, AnswerRequest request) {
        QuizAttempt attempt = quizAttemptRepository.findById(request.getQuizAttemptId())
            .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        Question question = questionRepository.findById(request.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        boolean isCorrect = question.getCorrectAnswer().equals(request.getSelectedAnswer());
        
        UserAnswer answer = new UserAnswer();
        answer.setQuizAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedAnswer(request.getSelectedAnswer());
        answer.setIsCorrect(isCorrect);
        answer.setTimeTakenSeconds(request.getTimeTakenSeconds());
        
        userAnswerRepository.save(answer);
        
        if (isCorrect) {
            attempt.setCorrectAnswers(attempt.getCorrectAnswers() + 1);
            quizAttemptRepository.save(attempt);
        }
        
        questionService.incrementStats(question.getId(), isCorrect);
    }
    
    @Transactional
    public QuizResultDTO completeQuiz(Long userId, Long quizAttemptId, Integer totalTimeTaken) {
        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
            .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        attempt.setCompleted(true);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setTimeTakenSeconds(totalTimeTaken);
        
        double percentage = (attempt.getCorrectAnswers().doubleValue() / attempt.getTotalQuestions()) * 100;
        attempt.setScorePercentage(percentage);
        
        quizAttemptRepository.save(attempt);
        
        userService.updateUserStats(userId, attempt.getCorrectAnswers(), attempt.getTotalQuestions());
        userService.updateStreak(userId);
        
        return toResultDTO(attempt);
    }
    
    public List<QuizResultDTO> getUserQuizHistory(Long userId) {
        return quizAttemptRepository.findByUserIdAndCompletedOrderByStartedAtDesc(userId, true)
            .stream()
            .map(this::toResultDTO)
            .collect(Collectors.toList());
    }
    
    public QuizResultDTO getQuizResult(Long userId, Long quizAttemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
            .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return toResultDTO(attempt);
    }
    
    private QuizResultDTO toResultDTO(QuizAttempt attempt) {
        QuizResultDTO dto = new QuizResultDTO();
        dto.setId(attempt.getId());
        dto.setUserId(attempt.getUser().getId());
        dto.setQuizType(attempt.getQuizType());
        dto.setTotalQuestions(attempt.getTotalQuestions());
        dto.setCorrectAnswers(attempt.getCorrectAnswers());
        dto.setScorePercentage(attempt.getScorePercentage());
        dto.setTimeTakenSeconds(attempt.getTimeTakenSeconds());
        dto.setCompletedAt(attempt.getCompletedAt());
        
        if (attempt.getCategory() != null) {
            dto.setCategoryId(attempt.getCategory().getId());
            dto.setCategoryName(attempt.getCategory().getNameAr());
        }
        
        return dto;
    }
}
