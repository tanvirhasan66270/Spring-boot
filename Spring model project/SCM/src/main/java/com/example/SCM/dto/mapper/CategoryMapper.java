package com.example.SCM.dto.mapper;

import com.example.SCM.dto.response.CategoryResponseDTO;
import com.example.SCM.dto.request.CategoryRequestDTO;
import com.example.SCM.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    // CategoryRequestDTO থেকে Category Entity-তে রূপান্তর (Create Operations)

    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());

        return category;
    }

   // Category Entity থেকে CategoryResponseDTO-তে রূপান্তর (Read Operations)

    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());

        return dto;
    }

   //এক্সিস্টিং Category Entity-কে Request DTO-র ডাটা দিয়ে আপডেট করা (Update Operations)

    public void updateEntity(CategoryRequestDTO dto, Category category) {
        if (dto == null || category == null) {
            return;
        }

        if (dto.getCategoryName() != null && !dto.getCategoryName().trim().isEmpty()) {
            category.setCategoryName(dto.getCategoryName());
        }

        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
    }
}
