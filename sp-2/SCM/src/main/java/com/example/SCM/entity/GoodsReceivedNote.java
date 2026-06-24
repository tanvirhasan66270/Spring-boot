package com.example.SCM.entity;

import com.example.SCM.enumClass.GRNStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods_received_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceivedNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grn_number", unique = true, nullable = false)
    private String grnNumber; // GoodsReceivedNoteNumber auto generate

    private Integer quantity; // Auto loaded from PurchaseOrder

    @Column(name = "received_quantity", nullable = false)
    private int receivedQuantity;

    @Column(name = "received_at", nullable = false)
    private LocalDate receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GRNStatus status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "inspection_date")
    private LocalDate inspectionDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── System Core / Warehouse Relations ────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by", nullable = false)
    private User receivedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspected_by")
    private User inspectedBy;

    @OneToMany(mappedBy = "goodsReceivedNote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GRNLineItem> lineItems = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}