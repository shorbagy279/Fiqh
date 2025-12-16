package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.*;
import com.fiqhmaster.exception.ResourceNotFoundException;
import com.fiqhmaster.exception.UnauthorizedException;
import com.fiqhmaster.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.info("Starting quiz for user {}: type={}, category={}", 
            userId, request.getQuizType(), request.getCategoryId());
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("المستخدم غير موجود"));
        
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuizType(request.getQuizType());
        attempt.setTotalQuestions(request.getQuestionCount());
        attempt.setCorrectAnswers(0);
        attempt.setCompleted(false);
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("القسم غير موجود"));
            attempt.setCategory(category);
        }
        
        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        log.info("Quiz started with ID: {}", savedAttempt.getId());
        return savedAttempt;
    }
    
    @Transactional
    public void submitAnswer(Long userId, AnswerRequest request) {
        log.debug("Submitting answer for user {}: question={}, answer={}", 
            userId, request.getQuestionId(), request.getSelectedAnswer());
        
        QuizAttempt attempt = quizAttemptRepository.findById(request.getQuizAttemptId())
            .orElseThrow(() -> new ResourceNotFoundException("محاولة الاختبار غير موجودة"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("غير مصرح لك بالوصول لهذا الاختبار");
        }
        
        if (attempt.getCompleted()) {
            throw new IllegalArgumentException("الاختبار مكتمل بالفعل");
        }
        
        Question question = questionRepository.findById(request.getQuestionId())
            .orElseThrow(() -> new ResourceNotFoundException("السؤال غير موجود"));
        
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
        log.info("Completing quiz {} for user {}", quizAttemptId, userId);
        
        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
            .orElseThrow(() -> new ResourceNotFoundException("محاولة الاختبار غير موجودة"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("غير مصرح لك بالوصول لهذا الاختبار");
        }
        
        if (attempt.getCompleted()) {
            log.warn("Quiz {} already completed", quizAttemptId);
            return toResultDTO(attempt);
        }
        
        attempt.setCompleted(true);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setTimeTakenSeconds(totalTimeTaken);
        
        if (attempt.getTotalQuestions() > 0) {
            double percentage = (attempt.getCorrectAnswers().doubleValue() / attempt.getTotalQuestions()) * 100;
            attempt.setScorePercentage(percentage);
        }
        
        quizAttemptRepository.save(attempt);
        
        userService.updateUserStats(userId, attempt.getCorrectAnswers(), attempt.getTotalQuestions());
        userService.updateStreak(userId);
        
        log.info("Quiz completed: score={}/{}, percentage={}%", 
            attempt.getCorrectAnswers(), attempt.getTotalQuestions(), attempt.getScorePercentage());
        
        return toResultDTO(attempt);
    }
    
    @Transactional(readOnly = true)
    public List<QuizResultDTO> getUserQuizHistory(Long userId) {
        return quizAttemptRepository.findByUserIdAndCompletedOrderByStartedAtDesc(userId, true)
            .stream()
            .map(this::toResultDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public QuizResultDTO getQuizResult(Long userId, Long quizAttemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
            .orElseThrow(() -> new ResourceNotFoundException("محاولة الاختبار غير موجودة"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("غير مصرح لك بالوصول لهذا الاختبار");
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