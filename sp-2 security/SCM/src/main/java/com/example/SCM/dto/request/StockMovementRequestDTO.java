package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class StockMovementRequestDTO {
    private Long productId;
    private Long warehouseId;       // ডেস্টিনেশন বা মেইন ওয়ারহাউজ আইডি
    private Long sourceWarehouseId; // ফিক্স: String থেকে Long করা হলো (TRANSFER টাইপের জন্য সোর্স ওয়ারহাউজ আইডি, অন্যথায় null)
    private String movementType;    // INWARD, OUTWARD, TRANSFER, ADJUSTMENT
    private int quantity;
    private String referenceId;     // GRN-Code, Invoice-Code, etc.
    private Long performedBy;       // অ্যাকশন পরিচালনাকারী ইউজারের আইডি
    private String remarks;
}