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

    @Column(name = "lc_number", nullable = false, unique = true, length = 50)
    private String lcNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder; // FK → purchase_orders

    @Column(name = "po_number", length = 50)
    private String poNumber;

    //  LetterOfCredit.java ক্লাসের ভেতর রিলেশন কলাম হিসেবে যুক্ত করুন:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier; // FK → suppliers table
    
    @Column(name = "issuing_bank", nullable = false, length = 100)
    private String issuingBank;

    @Column(name = "shipment_inco_terms", nullable = false, length = 20)
    private String shipmentIncoTerms; // FOB, CIF, CFR ইত্যাদি

    @Column(name = "latest_shipment_date", nullable = false)
    private LocalDate latestShipmentDate;

    @Column(name = "port_of_loading", nullable = false, length = 100)
    private String portOfLoading;

    @Column(name = "port_of_discharge", nullable = false, length = 100)
    private String portOfDischarge;

    @Column(name = "amendment_count", nullable = false)
    @Builder.Default
    private int amendmentCount = 0;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "lc_status", nullable = false, length = 20)
    @Builder.Default
    private LcStatus lcStatus = LcStatus.DRAFT;

    @Column(name = "document_vault_url", length = 512)
    private String documentVaultUrl;

    @Column(name = "opened_at")
    private LocalDate openedAt;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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

    //  বিজনেস ডোমেইন মেথড ──
    public void incrementAmendment() {
        if (this.lcStatus == LcStatus.OPENED || this.lcStatus == LcStatus.AMENDED) {
            this.lcStatus = LcStatus.AMENDED;
            this.amendmentCount += 1;
        }
    }
}