package com.example.SCM.entity;

import com.example.SCM.enumClass.DeliveryTripStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sendBy;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryTripStatus status;

    private String recipientSignature;

    private String deliveryPhotoUrl;

    @Column(nullable = false)
    private String customerAddress;

    private String vehicleInfo;

    private String destinationInfo;

    private String scheduleInfo;

    private String tripInfo;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── System Core Relations ────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        syncStatusTimestamps();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        syncStatusTimestamps();
    }

    private void syncStatusTimestamps() {
        if (this.status == DeliveryTripStatus.IN_TRANSIT && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        } else if (this.status == DeliveryTripStatus.DELIVERED && this.completedAt == null) {
            if (this.startedAt == null) {
                this.startedAt = LocalDateTime.now();
            }
            this.completedAt = LocalDateTime.now();
        }
    }
}