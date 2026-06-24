package com.example.SCM.entity;

import com.example.SCM.enumClass.UrgencyLevel;
import com.example.SCM.enumClass.PurchaseRequisitionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchase_requisitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_by", nullable = false)
    private Long requestedBy; // FK → Login User Id

    // Multi-product selection mapping
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "requisition_products",
            joinColumns = @JoinColumn(name = "requisition_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    // Multi-supplier selection mapping
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "requisition_suppliers",
            joinColumns = @JoinColumn(name = "requisition_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private List<Supplier> suppliers;

    @Column(nullable = false, length = 10)
    private String currency = "USD";

    @Column(name = "quantity_required", nullable = false)
    private int quantityRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    private UrgencyLevel  urgencyLevel;

    @Column(name = "required_by_date", nullable = false)
    private LocalDate requiredByDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private PurchaseRequisitionStatus approvalStatus = PurchaseRequisitionStatus.PENDING;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "lastUpdated_at", updatable = false)
    private LocalDateTime lastUpdate;




    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.approvalStatus == null) {
            this.approvalStatus = PurchaseRequisitionStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }



}