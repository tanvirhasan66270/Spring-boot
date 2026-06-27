package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class PurchaseOrderRequestDTO {

    private Long quotationId;
    private Long issuedBy;                 // লগইন করা ইউজারের আইডি
    private double totalAmount;
    private String currency;               // Default "USD"
    private String expectedDeliveryDate;   // "YYYY-MM-DD"
    private String status;                 // PurchaseOrderStatus (DRAFT, ORDERED, etc.)
}