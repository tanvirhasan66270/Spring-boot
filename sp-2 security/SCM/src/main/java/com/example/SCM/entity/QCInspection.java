package com.example.SCM.entity;

import com.example.SCM.enumClass.ResultStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qc_inspections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QCInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceivedNote goodsReceivedNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspected_by", nullable = false)
    private User inspectedBy;

    @Column(nullable = false)
    private String inspectionType;

    private int sampleSize;

    private int defectsFound;

    @Column(columnDefinition = "TEXT")
    private String defectDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultStatus result;


    private String certificateRef;


    private String labTestReport;

    @Column(nullable = false)
    private LocalDate inspectedAt;

    @OneToMany(mappedBy = "qcInspection", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QCChecklist> checklists = new ArrayList<>();

    @Column(updatable = false, nullable = false) // তৈরির সময় লক থাকবে
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.inspectedAt == null) {
            this.inspectedAt = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}