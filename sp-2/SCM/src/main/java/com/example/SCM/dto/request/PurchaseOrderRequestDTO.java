package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class PurchaseOrderRequestDTO {

    private String poNumber; // অপশনাল (যদি ফ্রন্টএন্ড ম্যানুয়ালি কোড দিতে চায়, না দিলে ব্যাকএন্ড অটো তৈরি করবে)
    private Long supplierId;
    private Long purchaseRequisitionId;
    private Long issuedBy;
    private double totalAmount;
    private String currency; // "USD"
    private String expectedDeliveryDate; // "YYYY-MM-DD" ফরম্যাটে স্ট্রিং ইনপুট আসবে
    private String status;
}
