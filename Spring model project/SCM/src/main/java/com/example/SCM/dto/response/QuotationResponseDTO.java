package com.example.SCM.dto.response;

import com.example.SCM.enumClass.QuotationStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class QuotationResponseDTO {
    private Long id;
    private String quotationNumber;
    private LocalDate validUntil;
    private int leadTimeDays;
    private boolean isSelected;
    private LocalDate receivedAt;
    private QuotationStatus status;
    private String productDescription;
    private double unitPrice;
    private int quantity;
    private double totalPrice;              // (unitPrice * quantity) আউটপুট
    private LocalDate deliveryTime;
    private String warranty;
    private String notes;
    private String attachmentUrl;
    private LocalDateTime createdAt;

    // Flattened Supplier Details
    private Long supplierId;
    private String supplierName;            // UI স্ক্রিনে সরাসরি সাপ্লায়ারের নাম দেখানোর জন্য

    // Flattened Product Details
    private Long productIds;                // ফ্রন্টএন্ড ইন্টারফেসের 'productIds' এর সাথে সিঙ্কড আইডি
    private String productName;

    // Flattened Purchase Requisition Details
    private Long purchaseRequisitionId;
          // রেফারেন্স হিসেবে স্ক্রিনে দেখানোর জন্য
}