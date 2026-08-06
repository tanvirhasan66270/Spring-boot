package com.example.SCM.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InvoiceHistoryResponseDTO {
    private Long id;
    private Long invoiceId;
    private String invoiceNumber;
    private double totalAmount;
    private double paidAmount;
    private double dueAmount;
    private String paymentStatus;
    private String invoiceStatus;
    private String notes;
    private LocalDateTime modifiedAt;
}