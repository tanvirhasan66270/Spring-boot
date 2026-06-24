package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String phone;

    private String address;

    @Column(name = "nid_number")
    private String nidNumber;

    private String gender; // MALE / FEMALE / OTHER

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    private String dob;

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double rating = 0.0;

    @Column(name = "total_deliveries", columnDefinition = "INT DEFAULT 0")
    private Integer totalDeliveries = 0;

    @Column(name = "total_earnings", columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double totalEarnings = 0.0;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    private String image;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Warehouse / Auth Management ──────────────────────────────
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "driver_warehouses",
            joinColumns = @JoinColumn(name = "driver_id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id")
    )
    @Builder.Default
    private Set<Warehouse> warehouses = new HashSet<>();

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