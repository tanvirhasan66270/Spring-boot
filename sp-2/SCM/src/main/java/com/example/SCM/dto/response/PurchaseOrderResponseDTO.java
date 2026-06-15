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
    private double totalAmount; // স্ক্রিন বা ড্যাশবোর্ডে প্রদর্শনের জন্য মূল অ্যামাউন্ট ফিল্ড
    private String currency;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Flattened Parents
    private Long supplierId;
    private String supplierName;
    private Long purchaseRequisitionId;
}