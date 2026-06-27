package com.example.SCM.dto.mapper;

import com.example.SCM.dto.response.CategoryResponseDTO;
import com.example.SCM.dto.request.CategoryRequestDTO;
import com.example.SCM.entity.Category;
import org.springframework.stereotype.Component;

/**
 * CategoryMapper
 *
 * Responsible for converting:
 * 1. CategoryRequestDTO -> Category Entity
 * 2. Category Entity -> CategoryResponseDTO
 *
 * This class helps separate API models (DTOs)
 * from database entities.
 */
@Component
public class CategoryMapper {

    /**
     * Convert CategoryRequestDTO to Category Entity.
     *
     * Used during category creation.
     *
     * @param dto Incoming request data from client
     * @return Category entity ready for persistence
     */
    public Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());

        return category;
    }

    /**
     * Convert Category Entity to CategoryResponseDTO.
     *
     * Used when sending category information back to the client.
     *
     * @param category Category entity from database
     * @return CategoryResponseDTO
     */
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

    /**
     * Update existing Category Entity with request data.
     *
     * Used during category modification.
     *
     * @param dto Incoming request data containing updates
     * @param category Existing entity instance to be modified
     */
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