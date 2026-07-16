package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class QuotationRequestDTO {
    private Long supplierId;
    private Long purchaseRequisitionId;
    private int leadTimeDays;
    private boolean isSelected;
    private String receivedAt;              // "YYYY-MM-DD"
    private String status;                  // "PENDING", "UNDER_REVIEW" ইত্যাদি
    private String productDescription;
    private double unitPrice;
    private int quantity;                   // PR থেকে ফ্রন্টএন্ডে রিড হওয়া quantityRequired ভ্যালু
    private String deliveryTime;            // "YYYY-MM-DD"
    private String warranty;
    private String notes;
    private String attachmentUrl;
}