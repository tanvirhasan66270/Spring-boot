package com.example.SCM.dto.response;

import lombok.Data;

/**
 * CategoryResponseDTO
 *
 * Response Data Transfer Object used to send Category information
 * from backend to frontend (API response).
 *
 * This DTO is used in:
 * - Get Category By ID API
 * - Get All Categories API
 * - Category Management Dashboard
 */
@Data
public class CategoryResponseDTO {

    /**
     * Unique Category ID
     *
     * This is the primary identifier of the Category entity.
     */
    private Long id;

    /**
     * Name of the category
     */
    private String categoryName;

    /**
     * Detailed description of the category
     */
    private String description;

}