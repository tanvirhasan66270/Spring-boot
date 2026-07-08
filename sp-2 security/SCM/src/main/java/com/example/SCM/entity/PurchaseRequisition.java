package com.example.SCM.entity;

import com.example.SCM.enumClass.UrgencyLevel;
import com.example.SCM.enumClass.PurchaseRequisitionStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchase_requisitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequisition {
    


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requestedBy;

    // Multi-product selection mapping
    @JsonIgnore
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

    @Column(nullable = false)
    private String currency ;

    private int quantityRequired;

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel;

    @Column(nullable = false)
    private LocalDate requiredByDate;

    @Enumerated(EnumType.STRING)
     @Builder.Default
    private PurchaseRequisitionStatus approvalStatus = PurchaseRequisitionStatus.PENDING;

    private Long approvedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.approvalStatus == null) {
            this.approvalStatus = PurchaseRequisitionStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}