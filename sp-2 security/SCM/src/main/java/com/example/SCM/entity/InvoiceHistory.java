package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🌟 সঠিক নিয়মে invoice_id কলাম ম্যাপিং (insertable এবং updatable বাদ দেওয়া হয়েছে)
    @Column(name = "invoice_id", nullable = false)
    private Long invoiceId;

    private String invoiceNumber;
    private double totalAmount;
    private double paidAmount;
    private double dueAmount;
    private String paymentStatus;
    private String invoiceStatus;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime modifiedAt;
}