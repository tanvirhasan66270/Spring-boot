package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class QuotationRequestDTO {
    private Long supplierId;
    private Long productIds;               // ফ্রন্টএন্ড ইন্টারফেসের 'productIds' নামের সাথে বাইন্ডিং সিঙ্ক রাখার জন্য
    private String productName;
    private Long purchaseRequisitionId;    // এই আইডি সিলেক্ট হলে ফ্রন্টএন্ড থ্রু-তে quantity রিড হবে
    private String validUntil;              // "YYYY-MM-DD"
    private int leadTimeDays;
    private boolean isSelected;
    private String receivedAt;              // "YYYY-MM-DD"
    private String status;                  // "PENDING", "UNDER_REVIEW" ইত্যাদি
    private String productDescription;
    private double unitPrice;
    private int quantity;                   // ফ্রন্টএন্ড ফর্ম সাবমিশনের সময় আসা রিকুইজিশনের quantityRequired ভ্যালু
    private String deliveryTime;            // "YYYY-MM-DD"
    private String warranty;
    private String notes;
    private String attachmentUrl;
}