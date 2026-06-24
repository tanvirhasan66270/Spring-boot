package com.example.SCM.entity;

import com.example.SCM.enumClass.ReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; // FK → User (LOGISTICS_OFFICER ID)

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId; // FK → Warehouse / Branch Location

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate; // Tracking Date (YYYY-MM-DD)

    @Column(name = "total_tasks_done", nullable = false)
    private int totalTasksDone;

    @Column(name = "issues_logged", nullable = false)
    private int issuesLogged;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false, length = 20)
    @Builder.Default
    private ReportStatus reportStatus = ReportStatus.DRAFT;

    @Column(name = "attachment_url", length = 512)
    private String attachmentUrl;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.generatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}