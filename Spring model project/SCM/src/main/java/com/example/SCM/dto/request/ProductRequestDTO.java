package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private Long id;
    private String productCode;
    private String name;
    private String unit;
    private int reorderPoint;
    private double unitCost;
    private int quantity;
    private double sellingPrice;
    private String hasExpiryDate;
    private double weight; // ➕ নতুন ফিল্ড যোগ করা হলো
    private boolean isActive;
    private String availability;
    private String image; // Base64
    private Long categoryId;
}