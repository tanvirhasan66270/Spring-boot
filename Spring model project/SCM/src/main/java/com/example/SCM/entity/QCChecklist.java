package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "qc_checklists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QCChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK → QCInspection (Parent Relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", nullable = false)
    private QCInspection qcInspection;

    @Column(name = "checkpoint_name", nullable = false, length = 150)
    private String checkpointName;

    @Column(name = "is_passed", nullable = false)
    private boolean isPassed;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks; // description as requested

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}