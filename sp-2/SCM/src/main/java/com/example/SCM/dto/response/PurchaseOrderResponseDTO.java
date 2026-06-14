package com.example.SCM.dto.response;

import com.example.SCM.enumClass.PurchaseOrderStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderResponseDTO {
    private Long id;
    private String poNumber;
    private Long issuedBy;
    private double totalAmount;
    private double grandTotal; // ডাইনামিক গ্র্যান্ড টোটাল যা UI স্ক্রিনে মূল টাকা দেখাবে
    private String currency;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Flattened Parents
    private Long supplierId;
    private String supplierName;
    private Long purchaseRequisitionId;

    // চাইল্ড লাইন আইটেমের কালেকশন গ্রিড
    private List<POLineItemResponseDTO> lineItems;
}