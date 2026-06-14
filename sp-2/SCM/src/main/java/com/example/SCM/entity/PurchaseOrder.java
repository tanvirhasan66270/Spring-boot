package com.example.SCM.entity;

import com.example.SCM.enumClass.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String poNumber;

    @Column(name = "issued_by", nullable = false)
    private Long issuedBy;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount; // বেস বা ওল্ড লাইন আইটেম ট্র্যাকের ব্যাকআপ

    @Column(name = "grand_total", nullable = false)
    private double grandTotal; // রোল-আপ লজিকের মাধ্যমে ডাইনামিকালি ক্যালকুলেটেড ফাইনাল অ্যামাউন্ট

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    @Column(name = "expected_delivery_date", nullable = false)
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_requisition_id", nullable = false)
    private PurchaseRequisition purchaseRequisition;

    // 💡 এটি যুক্ত করা হয়েছে: গ্র্যান্ড টোটাল রোল-আপ এবং অটো ডিলিশন (Orphan Removal) এর জন্য
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<POLineItem> lineItems = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = PurchaseOrderStatus.DRAFT;
        }
        if (this.poNumber == null) {
            this.poNumber = "PO-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}