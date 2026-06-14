package com.example.SCM.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseOrderRequestDTO {
    private String poNumber;
    private Long issuedBy;
    private Long supplierId;
    private Long purchaseRequisitionId;
    private String expectedDeliveryDate; // "YYYY-MM-DD"
    private String status;               // "DRAFT", "ISSUED" etc.
    private List<POLineItemRequestDTO> lineItems; // একসাথে মাস্টার-ডিটেইল সেভ করার জন্য
}