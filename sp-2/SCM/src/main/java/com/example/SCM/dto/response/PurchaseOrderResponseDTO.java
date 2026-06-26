package com.example.SCM.dto.response;

import com.example.SCM.enumClass.PurchaseOrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PurchaseOrderResponseDTO {
    private Long id;
    private String poNumber;
    private Integer quantity; // Quotation থেকে আসা ফ্ল্যাট কোয়ান্টিটি
    private double totalAmount;
    private String currency;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private Long issuedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //Auto Loaded Supplier Details
    private Long supplierId;
    private String supplierName;
    private String supplierEmail;// TS ইন্টারফেসের 'supplierName' ফিল্ডের সাথে সিঙ্কড

    //Auto Loaded Purchase Requisition Details
    private Long purchaseRequisitionId;    // TS ইন্টারফেসের 'PurchaseRequisitionId' ফিল্ডের সাথে সিঙ্কড

    //Quotation Connection Details
    private Long quotationId;
}