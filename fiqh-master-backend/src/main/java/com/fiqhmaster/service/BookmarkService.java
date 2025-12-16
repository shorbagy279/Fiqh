package com.fiqhmaster.service;

import com.fiqhmaster.dto.QuestionDTO;
import com.fiqhmaster.entity.*;
import com.fiqhmaster.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    
    @Transactional
    public void addBookmark(Long userId, Long questionId, String notes) {
        if (bookmarkRepository.existsByUserIdAndQuestionId(userId, questionId)) {
            throw new RuntimeException("السؤال محفوظ بالفعل");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("السؤال غير موجود"));
        
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setQuestion(question);
        bookmark.setNotes(notes);
        
        bookmarkRepository.save(bookmark);
    }
    
    @Transactional
    public void removeBookmark(Long userId, Long questionId) {
        bookmarkRepository.deleteByUserIdAndQuestionId(userId, questionId);
    }
    
    @Transactional
    public void updateBookmarkNotes(Long userId, Long questionId, String notes) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndQuestionId(userId, questionId)
            .orElseThrow(() -> new RuntimeException("الإشارة المرجعية غير موجودة"));
        
        bookmark.setNotes(notes);
        bookmarkRepository.save(bookmark);
    }
    
    public List<QuestionDTO> getUserBookmarks(Long userId) {
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(bookmark -> toQuestionDTO(bookmark.getQuestion(), userId))
            .collect(Collectors.toList());
    }
    
    private QuestionDTO toQuestionDTO(Question question, Long userId) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setCategoryId(question.getCategory().getId());
        dto.setCategoryName(question.getCategory().getNameAr());
        dto.setQuestionAr(question.getQuestionAr());
        dto.setQuestionEn(question.getQuestionEn());
        dto.setDifficulty(question.getDifficulty());
        dto.setIsBookmarked(true);
        
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
        
        return dto;
    }
}