package com.example.SCM.entity;

import com.example.SCM.enumClass.POLineItemStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "po_line_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class POLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "line_total", nullable = false)
    private double lineTotal; // অটো ক্যালকুলেটেড ফিল্ড

    @Column(name = "quotation_ref")
    private String quotationRef;

    @Column(name = "po_number")
    private String poNumber;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "shipment_method")
    private String shipmentMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private POLineItemStatus status = POLineItemStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        calculateLineTotal();
        if (this.status == null) {
            this.status = POLineItemStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        calculateLineTotal();
    }

    // 💡 লজিক ২: স্ট্যাটাস CANCELLED হলে lineTotal = 0 করার কন্ডিশন
    private void calculateLineTotal() {
        if (this.status == POLineItemStatus.CANCELLED) {
            this.lineTotal = 0.0;
        } else {
            this.lineTotal = this.quantity * this.unitPrice;
        }
    }
}