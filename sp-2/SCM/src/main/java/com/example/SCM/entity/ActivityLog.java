package com.example.SCM.entity;

import com.example.SCM.enumClass.ActionStatus;
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

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "user_email", length = 100)
    private String userEmail;

    @Column(nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE, LOGIN

    @Column(nullable = false, length = 50)
    private String module; // PO, GRN, QC, LC, SHIPMENT, INVOICE

    @Column(name = "reference_id", nullable = false, length = 50)
    private String referenceId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    // 🎯 এনাম ক্লাসের ম্যাপিং যুক্ত করা হলো
    @Enumerated(EnumType.STRING)
    @Column(name = "action_status", nullable = false, length = 20)
    @Builder.Default
    private ActionStatus actionStatus = ActionStatus.SUCCESS;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }
}