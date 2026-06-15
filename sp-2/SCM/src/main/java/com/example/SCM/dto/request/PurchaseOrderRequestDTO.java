package com.example.SCM.dto.request;

import lombok.Data;

@Data
public class PurchaseOrderRequestDTO {
    private String poNumber;
    private Long issuedBy;
    private Long supplierId;
    private Long purchaseRequisitionId;
    private String expectedDeliveryDate; // "YYYY-MM-DD"
    private String status;               // "DRAFT", "ISSUED" ইত্যাদি
}