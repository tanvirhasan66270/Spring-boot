package com.example.SCM.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseRequisitionRequestDTO {
    private Long requestedBy;
    private List<Long> productIds;  // মাল্টিপল প্রোডাক্ট আইডি কালেকশন
    private List<Long> supplierIds; // মাল্টিপল সাপ্লায়ার আইডি কালেকশন
    private String currency;        // "USD"
    private int quantityRequired;
    private String urgencyLevel;    // "HIGH", "CRITICAL" ইত্যাদি
    private String requiredByDate;  // "YYYY-MM-DD" ফরম্যাট
    private String remarks;
}