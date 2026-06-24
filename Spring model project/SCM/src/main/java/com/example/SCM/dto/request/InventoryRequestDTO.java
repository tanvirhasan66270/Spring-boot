package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class InventoryRequestDTO {
    private Long productId;
    private Long warehouseId;
    private int quantityOnHand;
    private int quantityReserved;
    private String locationStatus;
    private String expiryDate; // ফ্রন্টএন্ড থেকে "YYYY-MM-DD" ফরম্যাটে স্ট্রিং ইনপুট আসবে
    private String stockStatus;
}
