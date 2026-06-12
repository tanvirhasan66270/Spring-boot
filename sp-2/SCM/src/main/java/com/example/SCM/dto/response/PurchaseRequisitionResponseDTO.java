package com.example.SCM.dto.response;


import com.example.SCM.enumClass.PurchaseRequisitionStatus;
import com.example.SCM.enumClass.UrgencyLevel;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class PurchaseRequisitionResponseDTO {

    private Long id;
    private Long requestedBy;
    private String currency;
    private int quantityRequired;
    private UrgencyLevel urgencyLevel;            // এনাম আউটপুট
    private LocalDate requiredByDate;
    private PurchaseRequisitionStatus approvalStatus; // এনাম আউটপুট
    private Long approvedBy;
    private String remarks;
    private LocalDateTime createdAt;

    // Flattened relational lists for Frontend UI grids
    private List<Long> productIds;
    private List<String> productNames;   // UI স্ক্রিনে সরাসরি কমা দিয়ে প্রোডাক্টের নাম দেখানোর জন্য

    private List<Long> supplierIds;
    private List<String> supplierNames;
}
