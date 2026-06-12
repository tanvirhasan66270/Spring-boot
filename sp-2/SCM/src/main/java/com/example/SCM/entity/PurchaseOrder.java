package com.example.SCM.entity;

import com.example.SCM.enumClass.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_number", nullable = false, unique = true, length = 50)
    private String poNumber; // অটো-জেনারেটেড ইউনিক পিও নাম্বার (যেমন: PO-2026-001)

    // সাপ্লায়ারের সাথে মেনি-টু-ওয়ান রিলেশন
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    // ১টি রিকুইজিশন থেকে সাধারণত ১টিই ফাইনাল পারচেজ অর্ডার ইস্যু হয়
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_id", nullable = false)
    private PurchaseRequisition purchaseRequisition;

    @Column(name = "issued_by", nullable = false)
    private Long issuedBy; // লগইন থাকা প্রোকিউরমেন্ট অফিসারের ইউজার আইডি

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    @Column(name = "expected_delivery_date", nullable = false)
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PurchaseOrderStatus.DRAFT;
        }
        // রিয়েল সিস্টেমে poNumber ফাঁকা থাকলে এখানে UUID বা কাস্টম সিকোয়েন্স লজিক বসানো যায়
        if (this.poNumber == null) {
            this.poNumber = "PO-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}