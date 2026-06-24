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

    @Column(name = "send_by", nullable = false)
    private String sendBy;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryTripStatus status;

    @Column(name = "recipient_signature")
    private String recipientSignature;

    @Column(name = "delivery_photo_url")
    private String deliveryPhotoUrl;

    @Column(name = "customer_address", nullable = false)
    private String customerAddress;

    @Column(name = "vehicle_info")
    private String vehicleInfo;

    @Column(name = "destination_info")
    private String destinationInfo;

    @Column(name = "schedule_info")
    private String scheduleInfo;

    @Column(name = "trip_info")
    private String tripInfo;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver; // Represented as User (Driver role)

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