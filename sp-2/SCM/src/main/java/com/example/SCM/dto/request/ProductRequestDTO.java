package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class ProductRequestDTO {

    private String productCode;
    private String name;
    private Long categoryId; // ফরেন কি রেফারেন্স আইডি
    private String unit;
    private int reorderPoint;
    private double unitCost;
    private int quantity;
    private double sellingPrice;
    private String hasExpiryDate;
    private String availability;
    private String image; // Base64 String input


}
