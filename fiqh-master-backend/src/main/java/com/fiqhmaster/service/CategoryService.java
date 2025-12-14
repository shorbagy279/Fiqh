package com.fiqhmaster.service;

import com.fiqhmaster.dto.CategoryDTO;
import com.fiqhmaster.entity.Category;
import com.fiqhmaster.repository.CategoryRepository;
import com.fiqhmaster.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        return toDTO(category);
    }
    
    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setNameAr(category.getNameAr());
        dto.setNameEn(category.getNameEn());
        dto.setDescriptionAr(category.getDescriptionAr());
        dto.setDescriptionEn(category.getDescriptionEn());
        dto.setIcon(category.getIcon());
        dto.setColor(category.getColor());
        dto.setDisplayOrder(category.getDisplayOrder());
        
        int questionCount = questionRepository.findByCategoryId(category.getId()).size();
        dto.setQuestionCount(questionCount);
        
        return dto;
    }
}