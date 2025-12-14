package com.fiqhmaster.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Long id;
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String descriptionEn;
    private String icon;
    private String color;
    private Integer displayOrder;
    private Integer questionCount;
}