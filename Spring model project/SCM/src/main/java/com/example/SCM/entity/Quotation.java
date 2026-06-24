package com.example.SCM.entity;

import com.example.SCM.enumClass.QuotationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quotation_number", nullable = false, unique = true, length = 50)
    private String quotationNumber; // ব্যাকএন্ড থেকে ওয়ান-টাইম অটো জেনারেট হবে

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_id", nullable = false)
    private PurchaseRequisition purchaseRequisition;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "lead_time_days", nullable = false)
    private int leadTimeDays;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected = false;

    @Column(name = "received_at", nullable = false)
    private LocalDate receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuotationStatus status = QuotationStatus.PENDING;

    @Column(name = "product_description", columnDefinition = "TEXT", nullable = false)
    private String productDescription;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private Integer quantity; // রিকুইজিশন থেকে ফ্রন্টএন্ডে লোড হওয়া quantityRequired ভ্যালু এখানে সেভ হবে

    @Column(name = "total_price", nullable = false)
    private double totalPrice; // অটো ক্যালকুলেটেড ফিল্ড (unitPrice * quantity)

    @Column(name = "delivery_time", nullable = false)
    private LocalDate deliveryTime;

    @Column(length = 50)
    private String warranty; // বছর বা ডেসক্রিপশন

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "attachment_url", length = 512)
    private String attachmentUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        calculateTotalPrice();

        if (this.status == null) {
            this.status = QuotationStatus.PENDING;
        }
        if (this.quotationNumber == null) {
            this.quotationNumber = "QTN-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        calculateTotalPrice();
    }

    // বিজনেস লজিক: টোটাল প্রাইস অটো-সিঙ্ক রাখার জন্য
    private void calculateTotalPrice() {
        this.totalPrice = this.unitPrice * this.quantity;
    }
}