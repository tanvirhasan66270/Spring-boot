package com.example.SCM.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseRequisitionRequestDTO {
    private Long requestedBy;
    private List<Long> productIds;  // মাল্টিপল প্রোডাক্ট আইডি কালেকশন
    private List<Long> supplierIds; // মাল্টিপল সাপ্লায়ার আইডি কালেকশন
    private String currency;        // "USD"
    private int quantityRequired;
    private String urgencyLevel;    // ফ্রন্টএন্ড থেকে "HIGH" বা "CRITICAL" স্ট্রিং আসবে, যা সার্ভারে এনামে কনভার্ট হবে
    private String requiredByDate;  // "YYYY-MM-DD" ফরম্যাটে স্ট্রিং ইনপুট
    private String remarks;
}