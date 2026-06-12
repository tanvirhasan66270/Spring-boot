package com.example.SCM.dto.response;

import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long id;
    private String productCode;
    private String name;
    private String unit;
    private int reorderPoint;
    private double unitCost;
    private int quantity;
    private double sellingPrice;
    private String hasExpiryDate;
    private boolean isActive;
    private String availability;
    private String image; // Base64 String output


    private Long categoryId;
    private String categoryName; // ফ্রন্টএন্ডে ক্যাটাগরির নাম সরাসরি রেন্ডার করার জন্য
}
