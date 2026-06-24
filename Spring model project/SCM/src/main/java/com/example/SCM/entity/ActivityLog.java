package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE, LOGIN

    @Column(nullable = false, length = 50)
    private String module; // PO, GRN, QC, LC, SHIPMENT

    @Column(name = "reference_id", nullable = false, length = 50)
    private String referenceId; // টেবিলে ম্যাপ হওয়া নির্দিষ্ট ডাটার Primary ID

    @Column(columnDefinition = "TEXT")
    private String description; // মানুষের পড়ার উপযোগী অডিট মেসেজ

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // ইউজারের নেটওয়ার্ক আইপি এড্রেস

    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }


}