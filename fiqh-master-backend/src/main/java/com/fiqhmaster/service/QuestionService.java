package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.Question;
import com.fiqhmaster.exception.ResourceNotFoundException;
import com.fiqhmaster.repository.QuestionRepository;
import com.fiqhmaster.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final BookmarkRepository bookmarkRepository;
    
    @Transactional(readOnly = true)
    public List<QuestionDTO> getRandomQuestions(int limit, Long userId) {
        log.info("Fetching {} random questions for user {}", limit, userId);
        List<Question> questions = questionRepository.findRandomQuestions(limit);
        
        if (questions.isEmpty()) {
            throw new ResourceNotFoundException("لا توجد أسئلة متاحة");
        }
        
        return questions.stream()
            .map(q -> toDTO(q, userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByCategory(Long categoryId, int limit, Long userId) {
        log.info("Fetching {} questions from category {} for user {}", limit, categoryId, userId);
        List<Question> questions = questionRepository.findRandomQuestionsByCategory(categoryId, limit);
        
        if (questions.isEmpty()) {
            throw new ResourceNotFoundException("لا توجد أسئلة في هذا القسم");
        }
        
        return questions.stream()
            .map(q -> toDTO(q, userId))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public QuestionAnswerDTO getQuestionWithAnswer(Long id, Long userId) {
        log.info("Fetching question {} with answer for user {}", id, userId);
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("السؤال غير موجود"));
        return toAnswerDTO(question, userId);
    }
    
    @Transactional
    public void incrementStats(Long questionId, boolean isCorrect) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("السؤال غير موجود"));
        
        question.setTimesAnswered(question.getTimesAnswered() + 1);
        if (isCorrect) {
            question.setTimesCorrect(question.getTimesCorrect() + 1);
        }
        
        questionRepository.save(question);
        log.debug("Updated stats for question {}: answered={}, correct={}", 
            questionId, question.getTimesAnswered(), question.getTimesCorrect());
    }
    
    private QuestionDTO toDTO(Question question, Long userId) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setCategoryId(question.getCategory().getId());
        dto.setCategoryName(question.getCategory().getNameAr());
        dto.setQuestionAr(question.getQuestionAr());
        dto.setQuestionEn(question.getQuestionEn());
        dto.setDifficulty(question.getDifficulty());
        
        dto.setOptionsAr(Arrays.asList(
            question.getOptionAAr(),
            question.getOptionBAr(),
            question.getOptionCAr(),
            question.getOptionDAr()
        ));
        
        dto.setOptionsEn(Arrays.asList(
            question.getOptionAEn(),
            question.getOptionBEn(),
            question.getOptionCEn(),
            question.getOptionDEn()
        ));
        
        if (userId != null) {
            dto.setIsBookmarked(bookmarkRepository.existsByUserIdAndQuestionId(userId, question.getId()));
        }
        
        return dto;
    }
    
    private QuestionAnswerDTO toAnswerDTO(Question question, Long userId) {
        QuestionAnswerDTO dto = new QuestionAnswerDTO();
        dto.setId(question.getId());
        dto.setCategoryId(question.getCategory().getId());
        dto.setCategoryName(question.getCategory().getNameAr());
        dto.setQuestionAr(question.getQuestionAr());
        dto.setQuestionEn(question.getQuestionEn());
        dto.setDifficulty(question.getDifficulty());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        dto.setExplanationAr(question.getExplanationAr());
        dto.setExplanationEn(question.getExplanationEn());
        dto.setReferenceAr(question.getReferenceAr());
        dto.setReferenceEn(question.getReferenceEn());
        
        dto.setOptionsAr(Arrays.asList(
            question.getOptionAAr(),
            question.getOptionBAr(),
            question.getOptionCAr(),
            question.getOptionDAr()
        ));
        
        dto.setOptionsEn(Arrays.asList(
            question.getOptionAEn(),
            question.getOptionBEn(),
            question.getOptionCEn(),
            question.getOptionDEn()
        ));
        
        if (userId != null) {
            dto.setIsBookmarked(bookmarkRepository.existsByUserIdAndQuestionId(userId, question.getId()));
        }
        
        return dto;
    }
}
