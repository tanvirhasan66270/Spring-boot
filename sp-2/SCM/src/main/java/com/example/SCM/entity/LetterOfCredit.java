package com.example.SCM.entity;

import com.example.SCM.Util.TrakingCode.TrackingCodeGenerator;
import com.example.SCM.enumClass.LcStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "letter_of_credits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LetterOfCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LC number unique
    @Column(nullable = false, unique = true)
    private String lcNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false)
    private String issuingBank;

    @Column(nullable = false)
    private String shipmentIncoTerms; // FOB, CIF, CFR ইত্যাদি

    @Column(nullable = false)
    private LocalDate latestShipmentDate;

    @Column(nullable = false)
    private String portOfLoading;

    @Column(nullable = false)
    private String portOfDischarge;

    @Builder.Default
    private int amendmentCount = 0;

    private double amount;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "USD";

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private LcStatus lcStatus = LcStatus.DRAFT;

    private String documentVaultUrl;

    private LocalDate openedAt;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.lcNumber == null) {
            this.lcNumber = TrackingCodeGenerator.generateTrackingCode();
        }

        if (this.purchaseOrder != null && this.poNumber == null) {
            this.poNumber = this.purchaseOrder.getPoNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        if (this.lcStatus == LcStatus.OPENED && this.openedAt == null) {
            this.openedAt = LocalDate.now();
        }
    }

    // ── বিজনেস ডোমেইন মেথড ──
    public void incrementAmendment() {
        if (this.lcStatus == LcStatus.OPENED || this.lcStatus == LcStatus.AMENDED) {
            this.lcStatus = LcStatus.AMENDED;
            this.amendmentCount += 1;
        }
    }
}