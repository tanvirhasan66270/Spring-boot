package com.example.SCM.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryResponseDTO {
    private Long id;

    // Product Details (ফ্ল্যাট করা হয়েছে যেন ফ্রন্টএন্ডে সরাসরি নাম রেন্ডার করা যায়)
    private Long productId;
    private String productCode;
    private String productName;

    // Warehouse Details
    private Long warehouseId;
    private String warehouseName;

    // Inventory Core Fields
    private int quantityOnHand;
    private int quantityReserved;
    private int availableQuantity; // (quantityOnHand - quantityReserved) ফ্রন্টএন্ডের সুবিধার জন্য ক্যালকুলেটেড ফিল্ড
    private String locationStatus;
    private LocalDate expiryDate;
    private String stockStatus;
    private LocalDateTime lastUpdated;
}
