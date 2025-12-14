package com.fiqhmaster.service;

import com.fiqhmaster.dto.*;
import com.fiqhmaster.entity.Question;
import com.fiqhmaster.repository.QuestionRepository;
import com.fiqhmaster.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final BookmarkRepository bookmarkRepository;
    
    public List<QuestionDTO> getRandomQuestions(int limit, Long userId) {
        return questionRepository.findRandomQuestions(limit).stream()
            .map(q -> toDTO(q, userId))
            .collect(Collectors.toList());
    }
    
    public List<QuestionDTO> getQuestionsByCategory(Long categoryId, int limit, Long userId) {
        return questionRepository.findRandomQuestionsByCategory(categoryId, limit).stream()
            .map(q -> toDTO(q, userId))
            .collect(Collectors.toList());
    }
    
    public QuestionAnswerDTO getQuestionWithAnswer(Long id, Long userId) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        return toAnswerDTO(question, userId);
    }
    
    public void incrementStats(Long questionId, boolean isCorrect) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        question.setTimesAnswered(question.getTimesAnswered() + 1);
        if (isCorrect) {
            question.setTimesCorrect(question.getTimesCorrect() + 1);
        }
        
        questionRepository.save(question);
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