package com.example.SCM.dto.response;

import com.example.SCM.enumClass.PurchaseOrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseOrderResponseDTO {
    private Long id;
    private String poNumber;
    private Long issuedBy;
    private double totalAmount;
    private String currency;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status; // এনাম আউটপুট
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Supplier Details Flattening
    private Long supplierId;
    private String supplierName;


    // Purchase Requisition Details Flattening
    private Long purchaseRequisitionId;
    private String requisitionUrgencyLevel; // রিকুইজিশনটি কতটা জরুরি ছিল তা পিও স্ক্রিনে দেখানোর জন্য
}
