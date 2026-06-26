package com.example.SCM.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String phone;

    private String address;

    private String nidNumber;

    private String gender; // MALE / FEMALE / OTHER

    @Column(nullable = false, unique = true)
    private String email;

    private String vehicleType;

    private String vehicleNumber;

    private String dob;

    // Builder.Default ব্যবহার করলে অবজেক্ট তৈরির সময় এগুলো ডিফল্ট মান পেয়ে যাবে, columnDefinition বাদ
    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    private Integer totalDeliveries = 0;

    @Builder.Default
    private Double totalEarnings = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    private String image;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // ── Warehouse / Auth Management ──────────────────────────────
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    // unique = true যুক্ত করা হলো ডাটা সুরক্ষার জন্য
    @JoinColumn(name = "user_id", nullable = false, unique = true)
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