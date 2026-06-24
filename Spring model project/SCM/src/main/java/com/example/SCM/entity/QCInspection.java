package com.example.SCM.entity;

import com.example.SCM.enumClass.ResultStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qc_inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "inspection_type", nullable = false, length = 150)
    private String inspectionType;

    @Column(name = "sample_size", nullable = false)
    private int sampleSize;

    @Column(name = "defects_found", nullable = false)
    private int defectsFound;

    @Column(name = "defect_description", columnDefinition = "TEXT")
    private String defectDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 20)
    private ResultStatus result;

    @Column(name = "certificate_ref", length = 100)
    private String certificateRef;

    @Column(name = "lab_test_report", length = 255)
    private String labTestReport;

    @Column(name = "inspected_at", nullable = false)
    private LocalDate inspectedAt;


    @OneToMany(mappedBy = "qcInspection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QCChecklist> checklists = new ArrayList<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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