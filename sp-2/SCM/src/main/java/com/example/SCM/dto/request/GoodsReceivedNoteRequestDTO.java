package com.example.SCM.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class GoodsReceivedNoteRequestDTO {
    private Long poId;               // ফ্রন্টএন্ড 'poId' এর সাথে সিঙ্কড
    private Long productId;          // ফ্রন্টএন্ড 'productId' এর সাথে সিঙ্কড
    private int receivedQuantity;
    private Long receivedBy;         // লগইন ইউজারের আইডি
    private Long warehouseId;        // ওয়ারহাউজ আইডি
    private String receivedAt;       // "YYYY-MM-DD"
    private String status;           // PENDING, RECEIVED, APPROVED ইত্যাদি
    private String remarks;
    private Long inspectedBy;        // ইন্সপেকশন করা ইউজারের আইডি (ঐচ্ছিক)
    private String inspectionDate;   // "YYYY-MM-DD" (ঐচ্ছিক)

    private List<GRNLineItemRequestDTO> lineItems;
}